CREATE TABLE nationalInsuranceNumber (
    national_insurance_id SERIAL PRIMARY KEY,
    national_insurance_number VARCHAR NOT NULL,
    person_id INTEGER,
    FOREIGN KEY(person_id) REFERENCES person(person_id)
);