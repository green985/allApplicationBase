alter table public.diary_quotes
    alter column quote set default '',
    add column thoughts text not null default '',
    add constraint diary_quotes_thoughts_length_check check (char_length(thoughts) <= 10000);
