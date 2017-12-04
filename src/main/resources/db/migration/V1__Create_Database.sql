CREATE TABLE address (
    id INTEGER PRIMARY KEY,
    lines VARCHAR,
    city VARCHAR,
    country VARCHAR,
    postcode VARCHAR,
    international_postcode VARCHAR
);

CREATE TABLE user (
    id INTEGER PRIMARY KEY,
    first_name VARCHAR,
    middle_name VARCHAR,
    surname VARCHAR,
    address INTEGER,
    date_of_birth DATE,
    FOREIGN KEY(address) REFERENCES address(id)
);

CREATE TABLE verifiedPid (
    pid VARCHAR PRIMARY KEY,
    user INTEGER,
    FOREIGN KEY(user) REFERENCES user(id)
);