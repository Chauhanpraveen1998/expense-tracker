-- ============================================================
-- V1: Initial Schema for Expense Tracker
-- ============================================================

-- Users table
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Devices (for multi-device sync)
CREATE TABLE devices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_name     VARCHAR(100),
    device_token    VARCHAR(255),
    last_sync_at    TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT uk_device_token UNIQUE (device_token)
);

-- Categories (system + user-defined)
CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(50) NOT NULL,
    icon            VARCHAR(50),
    color           VARCHAR(7),
    is_system       BOOLEAN DEFAULT FALSE,
    display_order   INTEGER DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT uk_category_name UNIQUE (user_id, name)
);

-- Transactions
CREATE TABLE transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id           UUID REFERENCES devices(id),
    
    amount              DECIMAL(12,2) NOT NULL,
    transaction_type    VARCHAR(10) NOT NULL CHECK (transaction_type IN ('CREDIT', 'DEBIT')),
    category_id         UUID REFERENCES categories(id),
    merchant            VARCHAR(255),
    
    payment_method      VARCHAR(20),
    account_mask        VARCHAR(20),
    reference_no        VARCHAR(50),
    
    transaction_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    
    description         TEXT,
    notes               TEXT,
    tags                TEXT[],
    
    source              VARCHAR(20) DEFAULT 'SMS',
    confidence_score    DECIMAL(3,2),
    is_verified         BOOLEAN DEFAULT FALSE,
    
    local_id            VARCHAR(100),
    sms_hash            VARCHAR(64),
    
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    synced_at           TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT uk_transaction_local_id UNIQUE (user_id, local_id),
    CONSTRAINT uk_transaction_sms_hash UNIQUE (user_id, sms_hash)
);

-- Budgets
CREATE TABLE budgets (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id         UUID REFERENCES categories(id),
    
    amount              DECIMAL(12,2) NOT NULL,
    period              VARCHAR(10) NOT NULL CHECK (period IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
    start_date          DATE NOT NULL,
    
    alert_threshold     DECIMAL(3,2) DEFAULT 0.80,
    is_active           BOOLEAN DEFAULT TRUE,
    
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Goals
CREATE TABLE goals (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    name                VARCHAR(100) NOT NULL,
    target_amount       DECIMAL(12,2) NOT NULL,
    saved_amount        DECIMAL(12,2) DEFAULT 0,
    target_date         DATE,
    
    icon                VARCHAR(50),
    color               VARCHAR(7),
    priority            INTEGER DEFAULT 0,
    is_completed        BOOLEAN DEFAULT FALSE,
    
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Goal contributions
CREATE TABLE goal_contributions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_id             UUID NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    amount              DECIMAL(12,2) NOT NULL,
    contributed_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    notes               TEXT
);

-- Indexes
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_at DESC);
CREATE INDEX idx_transactions_category ON transactions(user_id, category_id, transaction_at);
CREATE INDEX idx_transactions_type_date ON transactions(user_id, transaction_type, transaction_at);
CREATE INDEX idx_transactions_sync ON transactions(user_id, synced_at) WHERE synced_at IS NULL;
CREATE INDEX idx_budgets_user_active ON budgets(user_id) WHERE is_active = TRUE;
CREATE INDEX idx_goals_user_active ON goals(user_id) WHERE is_completed = FALSE;

-- Seed system categories
INSERT INTO categories (name, icon, color, is_system, display_order) VALUES
    ('Food & Dining', 'restaurant', '#FF6B6B', TRUE, 1),
    ('Transport', 'directions_car', '#4ECDC4', TRUE, 2),
    ('Shopping', 'shopping_bag', '#45B7D1', TRUE, 3),
    ('Entertainment', 'movie', '#96CEB4', TRUE, 4),
    ('Utilities', 'bolt', '#FFEAA7', TRUE, 5),
    ('Health', 'local_hospital', '#DDA0DD', TRUE, 6),
    ('Groceries', 'local_grocery_store', '#98D8C8', TRUE, 7),
    ('Travel', 'flight', '#F7DC6F', TRUE, 8),
    ('Education', 'school', '#BB8FCE', TRUE, 9),
    ('Investment', 'trending_up', '#82E0AA', TRUE, 10),
    ('Salary', 'account_balance_wallet', '#85C1E9', TRUE, 11),
    ('Uncategorized', 'help_outline', '#BDC3C7', TRUE, 99);
