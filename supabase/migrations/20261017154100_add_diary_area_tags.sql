create table public.diary_tags (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    area_id text not null check (area_id in ('WORK', 'BODY', 'MIND', 'CHARACTER', 'LIFE')),
    name text not null check (char_length(name) between 1 and 40),
    normalized_name text not null,
    usage_count bigint not null default 0 check (usage_count >= 0),
    last_used_at timestamptz null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (user_id, area_id, normalized_name)
);

create index diary_tags_user_area_recency_index
    on public.diary_tags (user_id, area_id, last_used_at desc nulls last);

alter table public.diary_tags enable row level security;
revoke all on table public.diary_tags from public, anon, authenticated;
grant select, insert, update, delete on table public.diary_tags to service_role;

alter table public.diary_entries
    add column tag_id uuid null references public.diary_tags(id) on delete set null;

create index diary_entries_tag_id_index on public.diary_entries (tag_id);

create function public.normalize_diary_tag()
returns trigger
language plpgsql
set search_path = public
as $$
begin
    new.name := btrim(regexp_replace(new.name, '[[:space:]]+', ' ', 'g'));
    new.normalized_name := lower(new.name);
    new.updated_at := clock_timestamp();
    return new;
end;
$$;

create trigger diary_tags_normalize
    before insert or update on public.diary_tags
    for each row execute function public.normalize_diary_tag();

create function public.get_or_create_diary_tag(p_user_id uuid, p_area_id text, p_name text)
returns setof public.diary_tags
language sql
set search_path = public
as $$
    insert into public.diary_tags (user_id, area_id, name, normalized_name)
    values (p_user_id, p_area_id, p_name, '')
    on conflict (user_id, area_id, normalized_name)
    do update set normalized_name = excluded.normalized_name
    returning *;
$$;

create function public.check_diary_entry_tag()
returns trigger
language plpgsql
set search_path = public
as $$
begin
    if new.tag_id is null then
        return new;
    end if;
    perform 1 from public.diary_tags
        where id = new.tag_id and user_id = new.user_id and area_id = new.area_id
        for update;
    if not found then
        raise exception using errcode = '22023', message = 'Tag must belong to the entry user and area';
    end if;
    if tg_op = 'INSERT' then
        update public.diary_tags
            set usage_count = usage_count + 1, last_used_at = clock_timestamp()
            where id = new.tag_id;
    elsif old.tag_id is distinct from new.tag_id then
        update public.diary_tags
            set usage_count = usage_count + 1, last_used_at = clock_timestamp()
            where id = new.tag_id;
    end if;
    return new;
end;
$$;

create trigger diary_entries_check_tag
    before insert or update of tag_id, user_id, area_id on public.diary_entries
    for each row execute function public.check_diary_entry_tag();

revoke all on function public.normalize_diary_tag() from public, anon, authenticated;
revoke all on function public.get_or_create_diary_tag(uuid, text, text) from public, anon, authenticated;
revoke all on function public.check_diary_entry_tag() from public, anon, authenticated;
grant execute on function public.normalize_diary_tag() to service_role;
grant execute on function public.get_or_create_diary_tag(uuid, text, text) to service_role;
grant execute on function public.check_diary_entry_tag() to service_role;
