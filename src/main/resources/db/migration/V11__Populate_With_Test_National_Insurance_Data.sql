INSERT INTO nationalInsuranceNumber (national_insurance_number, person_id)
    VALUES ('TL 02 28 20', (select person_id from person where surname = 'Ralestone' and date_of_birth = '1991-04-01' limit 1)),
    ('RA 39 36 66 B',  (select person_id from person where surname = 'Delacroux' and date_of_birth = '1949-04-02' limit 1)),
    ('OS 63 53 84 D',  (select person_id from person where surname = 'Bourhill' and date_of_birth = '1954-08-13' limit 1));

INSERT INTO address (lines, city, country, postcode)
    VALUES ('97 Academy Street', 'BESFORD', 'United Kingdom', 'WR8 0TY');

INSERT INTO person (first_name, middle_name, surname, address, date_of_birth)
    VALUES ('Smith', null, 'Aidan', (SELECT address_id FROM address WHERE postcode = 'WR8 0TY'), '1961-03-24');
INSERT INTO nationalInsuranceNumber (national_insurance_number, person_id)
    VALUES ('HM 28 15 29 B', (SELECT MAX(person_id) FROM person));

INSERT INTO person (first_name, middle_name, surname, address, date_of_birth)
    VALUES ('Smith', null, 'Aidan', (SELECT address_id FROM address WHERE postcode = 'WR8 0TY'), '1961-03-24');
INSERT INTO nationalInsuranceNumber (national_insurance_number, person_id)
    VALUES ('OS 24 25 19 D', (SELECT MAX(person_id) FROM person));