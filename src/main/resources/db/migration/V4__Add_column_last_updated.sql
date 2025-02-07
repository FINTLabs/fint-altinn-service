ALTER TABLE instance
    ADD last_updated TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE instance
    ALTER COLUMN last_updated SET NOT NULL;