alter table public.diary_entries
    alter column area_id drop not null,
    add column if not exists duration_seconds bigint,
    add column if not exists timer_started_at timestamptz,
    add column if not exists timer_ends_at timestamptz,
    add column if not exists timer_cancelled_at timestamptz,
    add column if not exists completed_at timestamptz;

alter table public.diary_entries
    drop constraint if exists diary_entries_duration_seconds_check,
    drop constraint if exists diary_entries_timer_dates_check,
    drop constraint if exists diary_entries_timer_cancelled_check;

alter table public.diary_entries
    add constraint diary_entries_duration_seconds_check check (
        duration_seconds is null or duration_seconds > 0
    ),
    add constraint diary_entries_timer_dates_check check (
        (timer_started_at is null and timer_ends_at is null)
        or (
            timer_started_at is not null
            and timer_ends_at is not null
            and timer_ends_at >= timer_started_at
            and duration_seconds is not null
            and duration_seconds > 0
        )
    ),
    add constraint diary_entries_timer_cancelled_check check (
        timer_cancelled_at is null or timer_started_at is not null
    );
