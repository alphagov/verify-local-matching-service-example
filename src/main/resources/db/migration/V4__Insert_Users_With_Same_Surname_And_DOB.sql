INSERT INTO address (lines, city, country, postcode)
    VALUES ('51 Gloddaeth Street', 'BIRDSMOOR GATE', 'United Kingdom', 'DT6 5JG'),
           ('8840 2nd Terrace', 'Ashley', 'United Kingdom', 'SN14 0ZZ');

INSERT INTO person (first_name, middle_name, surname, address, date_of_birth)
    VALUES ('Zachary', null, 'Humphries', (SELECT address_id FROM address WHERE postcode = 'E1 8DX'), '1970-01-02'),
           ('Arlin', 'Nomi', 'Humphries', (SELECT address_id FROM address WHERE postcode = 'SN14 0ZZ'), '1970-01-02');