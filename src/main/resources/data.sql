insert into klub (nazwa) values ('Widzew Lodz');
insert into klub (nazwa) values ('Gornik Zabrze');

insert into sezon (nazwa) values ('2025/2026');

insert into pilkarz (imie, nazwisko, klub_id) values ('Jan', 'Kowalski', (select id from klub where nazwa = 'Widzew Lodz'));
insert into pilkarz (imie, nazwisko, klub_id) values ('Piotr', 'Nowak', (select id from klub where nazwa = 'Gornik Zabrze'));

insert into mecz (gospodarz_id, gosc_id, sezon_id, data_rozpoczecia) values (
    (select id from klub where nazwa = 'Widzew Lodz'), 
    (select id from klub where nazwa = 'Gornik Zabrze'), 
    (select id from sezon where nazwa = '2025/2026'), 
    '2026-04-24 20:00:00'
);

insert into uczestnictwo_wmeczu (mecz_id, pilkarz_id, rola, minuta_wejscia, minuta_zejscia, zolte_kartki, czerwona_kartka, faule) 
values (
    (select id from mecz limit 1), 
    (select id from pilkarz where imie = 'Jan' and nazwisko = 'Kowalski'), 
    'Napastnik', 0, 90, 0, false, 1
);
insert into uczestnictwo_wmeczu (mecz_id, pilkarz_id, rola, minuta_wejscia, minuta_zejscia, zolte_kartki, czerwona_kartka, faule) 
values (
    (select id from mecz limit 1), 
    (select id from pilkarz where imie = 'Piotr' and nazwisko = 'Nowak'), 
    'Obronca', 0, 90, 1, false, 3
);

insert into bramka (mecz_id, strzelec_id, asystujacy_id, minuta, samobojcza) values (
    (select id from mecz limit 1), 
    (select id from pilkarz where imie = 'Jan' and nazwisko = 'Kowalski'), 
    (select id from pilkarz where imie = 'Piotr' and nazwisko = 'Nowak'), 
    '15', false
);
