-- V9: Fix expense_date column type (date -> timestamp)
ALTER TABLE expenses ALTER COLUMN expense_date TYPE TIMESTAMP USING expense_date::timestamp;
