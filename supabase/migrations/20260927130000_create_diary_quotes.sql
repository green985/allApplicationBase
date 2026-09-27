create table if not exists public.diary_quotes (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    quote_date date not null,
    quote text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint diary_quotes_user_date_unique unique (user_id, quote_date)
);

create index if not exists diary_quotes_user_date_index
    on public.diary_quotes (user_id, quote_date);

alter table public.diary_quotes enable row level security;
