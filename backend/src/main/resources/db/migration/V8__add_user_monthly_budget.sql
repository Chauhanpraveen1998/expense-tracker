-- V8: Add monthly_budget to users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS monthly_budget DECIMAL(15, 2) DEFAULT 50000.00;
