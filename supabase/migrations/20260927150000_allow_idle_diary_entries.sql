alter table public.diary_entries
    drop constraint if exists diary_entries_area_id_check;

alter table public.diary_entries
    add constraint diary_entries_area_id_check check (
        area_id in ('IDLE', 'WORK', 'BODY', 'HEALTH', 'MIND', 'CHARACTER', 'PEOPLE', 'LIFE')
    );

alter table public.diary_entries
    drop constraint if exists diary_entries_text_not_blank;
