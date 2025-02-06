ALTER TABLE instance
    ADD fint_org_id VARCHAR(255);

ALTER TABLE instance
    ALTER COLUMN instance_id DROP NOT NULL;