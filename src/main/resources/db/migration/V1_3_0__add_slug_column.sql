ALTER TABLE businesses ADD COLUMN slug VARCHAR(100);

UPDATE businesses SET slug = CONCAT(
    REGEXP_REPLACE(LOWER(name), '[^a-z0-9]+', '-'),
    '-',
    CAST(id AS CHAR)
);

ALTER TABLE businesses MODIFY COLUMN slug VARCHAR(100) NOT NULL;
ALTER TABLE businesses ADD CONSTRAINT uk_business_slug UNIQUE (slug);