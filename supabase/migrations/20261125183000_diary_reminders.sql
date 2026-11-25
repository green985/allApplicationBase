alter table public.diary_entries
    add column is_reminder boolean not null default false;

create index diary_entries_reminders_index on public.diary_entries (user_id, id)
    where is_reminder;

create function public.check_diary_reminder_timer()
returns trigger
language plpgsql
set search_path = public
as $$
begin
    if new.is_reminder and new.timer_started_at is not null
        and new.timer_started_at is distinct from old.timer_started_at then
        raise exception using errcode = '22023', message = 'Timer cannot start for a reminder';
    end if;
    return new;
end;
$$;

create trigger diary_reminder_timer_guard
    before update of timer_started_at on public.diary_entries
    for each row execute function public.check_diary_reminder_timer();

revoke all on function public.check_diary_reminder_timer() from public, anon, authenticated;
grant execute on function public.check_diary_reminder_timer() to service_role;

create function public.change_diary_reminder(p_user_id uuid, p_entry_id uuid, p_today date)
returns boolean
language plpgsql
set search_path = public
as $$
declare
    source public.diary_entries%rowtype;
begin
    select * into source from public.diary_entries
        where id = p_entry_id and user_id = p_user_id for update;
    if not found then
        raise exception using errcode = 'P0002', message = 'Kayıt bulunamadı.';
    end if;

    if p_today is null then
        if source.is_reminder then return true; end if;
        if source.timer_ends_at > clock_timestamp()
            and source.completed_at is null and source.timer_cancelled_at is null then
            raise exception using errcode = 'PT409', message = 'Önce çalışan zamanlayıcıyı durdurun.';
        end if;
        if source.area_id is null and source.text !~ '[^[:space:]]' and source.duration_seconds is null then
            raise exception using errcode = '22023', message = 'Yapılacak kaydı için alan, not veya süre gerekli.';
        end if;
        update public.diary_entries set is_reminder = true, updated_at = clock_timestamp()
            where id = source.id;
    else
        if not source.is_reminder then
            raise exception using errcode = 'PT409', message = 'Kayıt Yapılacaklar listesinde değil.';
        end if;
        if source.area_id is null and source.text !~ '[^[:space:]]' and source.duration_seconds is null then
            raise exception using errcode = '22023', message = 'Yeni kayıt için alan, not veya süre gerekli.';
        end if;
        insert into public.diary_entries (user_id, entry_date, area_id, text, tag_id, duration_seconds)
            values (p_user_id, p_today, source.area_id, source.text, source.tag_id, source.duration_seconds);
    end if;
    return true;
end;
$$;

revoke all on function public.change_diary_reminder(uuid, uuid, date) from public, anon, authenticated;
grant execute on function public.change_diary_reminder(uuid, uuid, date) to service_role;
