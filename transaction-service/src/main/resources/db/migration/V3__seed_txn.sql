-- Seed few transactions for demo
INSERT INTO transactions(user_id, card_id, amount_yen, status, merchant_name, category)
VALUES
(1, 1, 1200, 'SUCCESS', '7-Eleven', 'CONVENIENCE'),
(1, 1, 5400, 'SUCCESS', 'Amazon', 'ECOMMERCE'),
(1, 2, 3000, 'FAILED', 'JR East', 'TRANSPORT')
ON CONFLICT DO NOTHING;
