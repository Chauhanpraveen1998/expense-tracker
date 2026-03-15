-- V4: Budgets table
CREATE TABLE budgets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount      DECIMAL(12, 2) NOT NULL,
    month       DATE NOT NULL,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
