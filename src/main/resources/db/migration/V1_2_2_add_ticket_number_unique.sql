ALTER TABLE tickets
ADD COLUMN ticket_date DATE GENERATED ALWAYS AS (CAST(created_at AS DATE)) STORED;

ALTER TABLE tickets
ADD CONSTRAINT uk_ticket_number_per_day
UNIQUE (business_id, category_id, ticket_number, ticket_date);