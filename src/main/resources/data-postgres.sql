insert into users (email, password, username, first_name, last_name, place_of_living, deleted, salt, user_type)
values ('marko@av.com', '', 'host1', 'Marko', 'Marković', '', false, '', 1);
insert into users (email, password, username, first_name, last_name, place_of_living, deleted, salt, user_type)
values ('nikola@av.com', '', 'host2', 'Nikola', 'Nikolić', '', false, '', 1);
insert into users (email, password, username, first_name, last_name, place_of_living, deleted, salt, user_type)
values ('milos@av.com', '', 'guest1', 'Milos', 'Milosevic', '', false, '', 1);

insert into locations (street, number, city, postal_code, country,longitude, latitude)
    values ('Trg Dositelja Obradovića', '6', 'Novi Sad', '21000', 'Srbija', 12, 25);
insert into locations (street, number, city, postal_code, country,longitude, latitude)
    values ('Trg Dositelja Obradovića', '2', 'Novi Sad', '21000', 'Srbija', 70, 50);

insert into accommodations (name, location_id, min_number_of_guests, max_number_of_guests, automatically_accept_request, host_id)
    values ('Kućica', 1, 1, 3, false, 1);
insert into accommodations (name, location_id, min_number_of_guests, max_number_of_guests, automatically_accept_request, host_id)
    values ('Studio', 2, 1, 3, false, 1);

insert into accommodation_benefits (service_name, accommodation_id)
values ('WIFI', 1);

insert into availability_slots (start_date, end_date, valid, accommodation_id)
    values (DATE('2024-11-10 18:43:20'), DATE('2024-11-15 18:43:20'), true, 1);

insert into prices (start_date, end_date, value, type, deleted, accommodation_id)
    values (DATE('2024-07-10 18:43:20'), DATE('2024-07-15 18:43:20'), 170, 0, false, 1);
insert into prices (start_date, end_date, value, type, deleted, accommodation_id)
    values (DATE('2024-11-10 18:43:20'), DATE('2024-11-15 18:43:20'), 170, 0, false, 1);

insert into reservations (start_date, end_date, number_of_guests, price, cancelled, approved, guest_id, accommodation_id)
    values (DATE('2024-11-11 18:43:20'), DATE('2024-11-12 18:43:20'), 1, 170, false, true, 3, 1);
