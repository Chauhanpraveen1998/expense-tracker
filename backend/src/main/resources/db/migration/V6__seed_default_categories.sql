-- V6: Seed global default categories (user_id IS NULL = available to all users)
INSERT INTO categories (id, name, color, icon, user_id) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Food',          '#FF6B6B', 'restaurant',     NULL),
    ('22222222-2222-2222-2222-222222222222', 'Transport',     '#4ECDC4', 'directions_car', NULL),
    ('33333333-3333-3333-3333-333333333333', 'Shopping',      '#45B7D1', 'shopping_bag',   NULL),
    ('44444444-4444-4444-4444-444444444444', 'Entertainment', '#96CEB4', 'movie',          NULL),
    ('55555555-5555-5555-5555-555555555555', 'Bills',         '#FFEAA7', 'receipt',        NULL),
    ('66666666-6666-6666-6666-666666666666', 'Health',        '#DDA0DD', 'local_hospital', NULL),
    ('77777777-7777-7777-7777-777777777777', 'Other',         '#B0BEC5', 'more_horiz',     NULL)
ON CONFLICT (id) DO NOTHING;
