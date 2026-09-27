create table if not exists public.diary_entries (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    entry_date date not null,
    area_id text not null,
    text text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint diary_entries_area_id_check check (
        area_id in ('WORK', 'BODY', 'HEALTH', 'MIND', 'CHARACTER', 'PEOPLE', 'LIFE')
    ),
    constraint diary_entries_text_not_blank check (length(btrim(text)) > 0)
);

create index if not exists diary_entries_user_date_index
    on public.diary_entries (user_id, entry_date);

alter table public.diary_entries enable row level security;
