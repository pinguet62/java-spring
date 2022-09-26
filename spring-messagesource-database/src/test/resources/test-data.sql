-- Database: LocaleLanguage
insert into MESSAGE (code, locale, translation)
values ('key', 'de_DE', 'Deutsch');
-- Database: Locale
insert into MESSAGE (code, locale, translation)
values ('key', 'en', 'English');
-- Database: Locale+LocaleLanguage
insert into MESSAGE (code, locale, translation)
values ('key', 'fr', 'Français');
insert into MESSAGE (code, locale, translation)
values ('key', 'fr_FR', 'Français de France');
-- Default
insert into MESSAGE (code, locale, translation)
values ('key', 'it', 'Italiano');
