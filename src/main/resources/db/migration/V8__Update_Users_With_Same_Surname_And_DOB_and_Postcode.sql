INSERT INTO person (first_name, middle_name, surname, address, date_of_birth)
    VALUES ('Ivan', null, 'Johnson', (SELECT address_id FROM address WHERE postcode = 'SY4 0ZZ'), '1970-01-02'),
           ('Rob', 'Nomi', 'Johnson', (SELECT address_id FROM address WHERE postcode = 'SY4 0ZZ'), '1970-01-02');