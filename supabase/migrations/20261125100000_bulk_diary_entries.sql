create table public.diary_bulk_requests (
    user_id uuid not null references auth.users(id) on delete cascade,
    request_id uuid not null,
    entries jsonb not null,
    result jsonb,
    created_at timestamptz not null default now(),
    primary key (user_id, request_id)
);

alter table public.diary_bulk_requests enable row level security;
revoke all on table public.diary_bulk_requests from public, anon, authenticated;
grant select, insert, update on table public.diary_bulk_requests to service_role;

create function public.create_diary_entries_bulk(
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
        if jsonb_typeof(item) is distinct from 'object' then
            raise exception using errcode = '22023', message = position || '. kayıt — Kayıt nesne olmalıdır.';
        end if;
        if item - array['entryDate', 'areaId', 'text'] <> '{}'::jsonb then
            raise exception using errcode = '22023', message = position || '. kayıt — Desteklenmeyen alan.';
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
        if jsonb_typeof(item->'areaId') is distinct from 'string'
            or (item->>'areaId') not in ('WORK', 'BODY', 'MIND', 'CHARACTER', 'LIFE') then
            raise exception using errcode = '22023', message = position || '. kayıt — Alan geçersiz.';
        end if;
        if jsonb_typeof(item->'text') is distinct from 'string'
            or (item->>'text') !~ '[^[:space:]]'
            or char_length(item->>'text') > 3000 then
            raise exception using errcode = '22023',
                message = position || '. kayıt — Metin boş olamaz ve en fazla 3000 karakter olabilir.';
        end if;
    end loop;

    -- The unique key waits for concurrent submissions; the whole RPC is one transaction.
    insert into public.diary_bulk_requests (user_id, request_id, entries)
        values (p_user_id, p_request_id, p_entries)
        on conflict (user_id, request_id) do nothing;
    select * into strict previous from public.diary_bulk_requests
        where user_id = p_user_id and request_id = p_request_id for update;
    if previous.entries <> p_entries then
        raise exception using errcode = 'PT409',
            message = 'Bu requestId daha önce farklı içerikle kullanıldı.';
    end if;
    if previous.result is not null then
        return previous.result;
    end if;

    with input as materialized (
        select gen_random_uuid() as id, value, ordinality
        from jsonb_array_elements(p_entries) with ordinality
    ), inserted as (
        insert into public.diary_entries (id, user_id, entry_date, area_id, text)
        select id, p_user_id, (value->>'entryDate')::date, value->>'areaId', value->>'text'
        from input
        returning id, entry_date, area_id, text, created_at
    )
    select jsonb_agg(jsonb_build_object(
        'id', inserted.id,
        'entryDate', inserted.entry_date,
        'areaId', inserted.area_id,
        'text', inserted.text,
        'createdAt', inserted.created_at
    ) order by input.ordinality) into saved
    from inserted join input using (id);

    update public.diary_bulk_requests set result = saved
        where user_id = p_user_id and request_id = p_request_id;
    return saved;
end;
$$;

revoke all on function public.create_diary_entries_bulk(uuid, uuid, jsonb) from public, anon, authenticated;
grant execute on function public.create_diary_entries_bulk(uuid, uuid, jsonb) to service_role;
