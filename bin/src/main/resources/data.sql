insert into klub (id, nazwa) values (1, 'Mistrzem Polski Widzew');
insert into klub (id, nazwa) values (2, 'Górnik Zabrze');

insert into pilkarz (id, imie, nazwisko, klub_id) values (1, 'Nie', 'Wiem', 1);
insert into pilkarz (id, imie, nazwisko, klub_id) values (2, 'cO', 'dodac', 2);

alter sequence klub_seq restart with (select max(id) + 1 from klub);
alter sequence pilkarz_seq restart with (select max(id) + 1 from pilkarz);