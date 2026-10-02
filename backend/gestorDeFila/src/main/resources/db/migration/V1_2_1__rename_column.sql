ALTER TABLE categories 
RENAME COLUMN averageServiceTimeMinutes TO average_Service_Time_Minutes;

ALTER TABLE categories
    ADD COLUMN prefix VARCHAR(255) NULL;
