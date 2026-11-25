create or replace function public.create_diary_entries_bulk(
    p_user_id uuid,
    p_request_id uuid,
    p_entries jsonb
)
returns jsonb
language plpgsql
set search_path = public
as $$
declare
    item jsonb;
    position bigint;
    parsed_date date;
    seconds numeric;
    previous public.diary_bulk_requests%rowtype;
    saved jsonb;
begin
    if p_user_id is null or p_request_id is null
        or jsonb_typeof(p_entries) is distinct from 'array' then
        raise exception using errcode = '22023', message = 'Geçerli bir toplu kayıt isteği gerekli.';
    end if;
    if jsonb_array_length(p_entries) = 0 then
        raise exception using errcode = '22023', message = 'En az bir kayıt gerekli.';
    end if;

    for item, position in
        select value, ordinality from jsonb_array_elements(p_entries) with ordinality
    loop
        if jsonb_typeof(item) is distinct from 'object'
            or item - array['entryDate', 'areaId', 'text', 'tagId', 'durationSeconds', 'markAsCompleted'] <> '{}'::jsonb then
            raise exception using errcode = '22023', message = position || '. kayıt — Desteklenmeyen kayıt alanı.';
        end if;
        if jsonb_typeof(item->'entryDate') is distinct from 'string'
            or (item->>'entryDate') !~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' then
            raise exception using errcode = '22023', message = position || '. kayıt — Tarih geçersiz.';
        end if;
        begin
            parsed_date := (item->>'entryDate')::date;
            if to_char(parsed_date, 'YYYY-MM-DD') <> item->>'entryDate' then
                raise exception using errcode = '22007';
            end if;
        exception when invalid_datetime_format or datetime_field_overflow then
            raise exception using errcode = '22023', message = position || '. kayıt — Tarih geçersiz.';
        end;
        if item->>'areaId' is not null and (item->>'areaId') not in ('WORK', 'BODY', 'MIND', 'CHARACTER', 'LIFE') then
            raise exception using errcode = '22023', message = position || '. kayıt — Alan geçersiz.';
        end if;
        if item ? 'text' and (jsonb_typeof(item->'text') <> 'string' or char_length(item->>'text') > 3000) then
            raise exception using errcode = '22023', message = position || '. kayıt — text, en fazla 3000 karakterlik bir metin olmalıdır.';
        end if;
        if item->>'tagId' is not null and (item->>'areaId' is null or
            (item->>'tagId') !~* '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$') then
            raise exception using errcode = '22023', message = position || '. kayıt — Etiket bu alana ve hesabınıza ait olmalıdır.';
        end if;
        if item->>'durationSeconds' is not null then
            if jsonb_typeof(item->'durationSeconds') <> 'number' then
                raise exception using errcode = '22023', message = position || '. kayıt — durationSeconds, 1–86400 arasında bir tam sayı olmalıdır.';
            end if;
            seconds := (item->>'durationSeconds')::numeric;
            if seconds not between 1 and 86400 or trunc(seconds) <> seconds then
                raise exception using errcode = '22023', message = position || '. kayıt — durationSeconds, 1–86400 arasında bir tam sayı olmalıdır.';
            end if;
        end if;
        if item ? 'markAsCompleted' and jsonb_typeof(item->'markAsCompleted') <> 'boolean' then
            raise exception using errcode = '22023', message = position || '. kayıt — markAsCompleted, true veya false olmalıdır.';
        end if;
        if item->>'areaId' is null and coalesce(item->>'text', '') !~ '[^[:space:]]'
            and item->>'durationSeconds' is null and (item->'markAsCompleted') is distinct from 'true'::jsonb then
            raise exception using errcode = '22023', message = position || '. kayıt — Alan, not, süre veya Yapıldı seçimi gerekli.';
        end if;
    end loop;

    insert into public.diary_bulk_requests (user_id, request_id, entries)
        values (p_user_id, p_request_id, p_entries)
        on conflict (user_id, request_id) do nothing;
    select * into strict previous from public.diary_bulk_requests
        where user_id = p_user_id and request_id = p_request_id for update;
    if previous.entries <> p_entries then
        raise exception using errcode = 'PT409', message = 'Bu requestId daha önce farklı içerikle kullanıldı.';
    end if;
    if previous.result is not null then
        return previous.result;
    end if;

    -- Lock selected tags in a consistent order before the usage trigger updates them.
    perform tag.id from public.diary_tags tag
        where tag.user_id = p_user_id and tag.id in (
            select (value->>'tagId')::uuid from jsonb_array_elements(p_entries)
        )
        order by tag.id for update;
    select input.ordinality into position
    from jsonb_array_elements(p_entries) with ordinality as input
    where input.value->>'tagId' is not null and not exists (
        select 1 from public.diary_tags tag
        where tag.id = (input.value->>'tagId')::uuid
          and tag.user_id = p_user_id and tag.area_id = input.value->>'areaId'
    )
    order by input.ordinality limit 1;
    if found then
        raise exception using errcode = '22023', message = position || '. kayıt — Etiket bu alana ve hesabınıza ait olmalıdır.';
    end if;

    with input as materialized (
        select gen_random_uuid() as id, value, ordinality
        from jsonb_array_elements(p_entries) with ordinality
    ), inserted as (
        insert into public.diary_entries (
            id, user_id, entry_date, area_id, text, tag_id, duration_seconds, completed_at
        )
        select id, p_user_id, (value->>'entryDate')::date, value->>'areaId', coalesce(value->>'text', ''),
            (value->>'tagId')::uuid, (value->>'durationSeconds')::numeric::bigint,
            case when value->'markAsCompleted' = 'true'::jsonb then statement_timestamp() end
        from input
        returning id, entry_date, area_id, text, created_at, tag_id, duration_seconds, completed_at
    )
    select jsonb_agg(jsonb_build_object(
        'id', inserted.id,
        'entryDate', inserted.entry_date,
        'areaId', inserted.area_id,
        'text', inserted.text,
        'createdAt', inserted.created_at,
        'tag', case when tag.id is not null then
            jsonb_build_object('id', tag.id, 'name', tag.name, 'areaId', tag.area_id)
        end,
        'durationSeconds', inserted.duration_seconds,
        'completedAt', inserted.completed_at
    ) order by input.ordinality) into saved
    from inserted join input using (id)
    left join public.diary_tags tag on tag.id = inserted.tag_id;

    update public.diary_bulk_requests set result = saved
        where user_id = p_user_id and request_id = p_request_id;
    return saved;
end;
$$;
