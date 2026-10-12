alter table public.diary_entries
    alter column area_id drop not null,
    alter column text set default '';

alter table public.diary_entries
    drop constraint if exists diary_entries_text_not_blank;

create or replace function public.check_diary_entry_timer_duration()
returns trigger
language plpgsql
set search_path = public
as $$
begin
    if new.duration_seconds is distinct from old.duration_seconds
        and old.timer_started_at is not null
        and old.timer_ends_at > clock_timestamp()
        and old.timer_cancelled_at is null
        and old.completed_at is null then
        raise exception using errcode = '22023', message = 'Running timer duration cannot change';
    end if;
    return new;
end;
$$;

create trigger diary_entries_timer_duration_guard
    before update of duration_seconds on public.diary_entries
    for each row execute function public.check_diary_entry_timer_duration();

create or replace function public.start_diary_entry_timer(p_entry_id uuid, p_user_id uuid)
returns setof public.diary_entries
language plpgsql
set search_path = public
as $$
declare
    entry public.diary_entries%rowtype;
    started_at timestamptz;
begin
    perform pg_advisory_xact_lock(hashtextextended(p_user_id::text, 0));
    select * into entry from public.diary_entries
        where id = p_entry_id and user_id = p_user_id for update;
    if not found then
        raise exception using errcode = 'P0002', message = 'Entry not found';
    end if;
    if entry.duration_seconds is null or entry.duration_seconds <= 0
        or entry.timer_started_at is not null
        or entry.completed_at is not null
        or entry.timer_cancelled_at is not null then
        raise exception using errcode = '22023', message = 'Timer cannot start';
    end if;
    started_at := clock_timestamp();
    if exists (
        select 1 from public.diary_entries
        where user_id = p_user_id
            and timer_started_at is not null
            and timer_ends_at > started_at
            and timer_cancelled_at is null
            and completed_at is null
    ) then
        raise exception using errcode = 'P0001', message = 'Another timer is running';
    end if;
    return query update public.diary_entries
        set timer_started_at = started_at,
            timer_ends_at = started_at + entry.duration_seconds * interval '1 second',
            updated_at = started_at
        where id = p_entry_id and user_id = p_user_id
        returning *;
end;
$$;

create or replace function public.cancel_diary_entry_timer(p_entry_id uuid, p_user_id uuid)
returns setof public.diary_entries
language plpgsql
set search_path = public
as $$
declare
    entry public.diary_entries%rowtype;
    cancelled_at timestamptz;
begin
    select * into entry from public.diary_entries
        where id = p_entry_id and user_id = p_user_id for update;
    if not found then
        raise exception using errcode = 'P0002', message = 'Entry not found';
    end if;
    if entry.timer_cancelled_at is not null then
        return next entry;
        return;
    end if;
    cancelled_at := clock_timestamp();
    if entry.timer_started_at is null or entry.timer_ends_at <= cancelled_at
        or entry.completed_at is not null then
        raise exception using errcode = '22023', message = 'Timer is not active';
    end if;
    return query update public.diary_entries
        set timer_cancelled_at = cancelled_at, updated_at = cancelled_at
        where id = p_entry_id and user_id = p_user_id
        returning *;
end;
$$;

revoke execute on function public.start_diary_entry_timer(uuid, uuid) from public, anon, authenticated;
revoke execute on function public.cancel_diary_entry_timer(uuid, uuid) from public, anon, authenticated;
grant execute on function public.start_diary_entry_timer(uuid, uuid) to service_role;
grant execute on function public.cancel_diary_entry_timer(uuid, uuid) to service_role;
