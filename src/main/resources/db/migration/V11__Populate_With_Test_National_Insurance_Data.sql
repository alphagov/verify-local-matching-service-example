INSERT INTO nationalInsuranceNumber (national_insurance_number, person_id)
    VALUES ('TL 02 28 20', (select person_id from person where surname = 'Ralestone' and date_of_birth = '1991-04-01' limit 1)),
    ('RA 39 36 66 B',  (select person_id from person where surname = 'Delacroux' and date_of_birth = '1949-04-02' limit 1)),
    ('OS 63 53 84 D',  (select person_id from person where surname = 'Bourhill' and date_of_birth = '1954-08-13' limit 1));