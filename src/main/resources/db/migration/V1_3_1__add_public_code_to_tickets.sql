ALTER TABLE tickets ADD COLUMN public_code VARCHAR(8) NOT NULL;
UPDATE tickets SET public_code = UPPER(SUBSTRING(MD5(CONCAT(RAND(), id)), 1, 6));
ALTER TABLE tickets ADD CONSTRAINT uk_tickets_public_code UNIQUE (public_code);