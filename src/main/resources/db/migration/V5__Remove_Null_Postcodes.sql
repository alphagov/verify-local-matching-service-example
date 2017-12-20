DELETE FROM person
WHERE address IN (SELECT address_id FROM address WHERE postcode IS NULL);

DELETE FROM address
WHERE postcode IS NULL;