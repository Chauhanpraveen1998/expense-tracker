-- V5: Performance indexes
CREATE INDEX idx_expenses_user_id      ON expenses(user_id);
CREATE INDEX idx_expenses_category_id  ON expenses(category_id);
CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);
CREATE INDEX idx_budgets_user_id       ON budgets(user_id);
