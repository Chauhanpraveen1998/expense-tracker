-- V7: Add accounts and enhance expenses table
-- Create accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0,
    bank_name VARCHAR(100),
    last_four_digits VARCHAR(4),
    color_primary VARCHAR(7) DEFAULT '#1E3A8A',
    color_secondary VARCHAR(7) DEFAULT '#3B82F6',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on accounts
CREATE INDEX IF NOT EXISTS idx_accounts_user ON accounts(user_id);

-- Add new columns to expenses table
ALTER TABLE expenses 
ADD COLUMN IF NOT EXISTS account_id UUID REFERENCES accounts(id) ON DELETE SET NULL,
ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(10) DEFAULT 'EXPENSE',
ADD COLUMN IF NOT EXISTS merchant_name VARCHAR(200),
ADD COLUMN IF NOT EXISTS note VARCHAR(500),
ADD COLUMN IF NOT EXISTS merchant_logo_url VARCHAR(500),
ADD COLUMN IF NOT EXISTS is_recurring BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS sms_hash VARCHAR(64);

-- Create expense_tags table
CREATE TABLE IF NOT EXISTS expense_tags (
    expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (expense_id, tag)
);

-- Update existing expenses: set merchant_name from description if null
UPDATE expenses 
SET merchant_name = COALESCE(description, 'Unknown') 
WHERE merchant_name IS NULL;

-- Make merchant_name NOT NULL after populating
ALTER TABLE expenses 
ALTER COLUMN merchant_name SET NOT NULL;

-- Create indexes for new columns
CREATE INDEX IF NOT EXISTS idx_expense_account ON expenses(account_id);
CREATE INDEX IF NOT EXISTS idx_expense_type ON expenses(transaction_type);
CREATE INDEX IF NOT EXISTS idx_expense_merchant ON expenses(merchant_name);
CREATE INDEX IF NOT EXISTS idx_expense_sms_hash ON expenses(sms_hash);
