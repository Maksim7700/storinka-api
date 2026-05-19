-- Align seed template prices with the catalog UI:
-- one-time license fee + recurring monthly subscription.

UPDATE templates SET license_price = 3990.00, monthly_price = 990.00
WHERE key IN ('beauty-salon', 'restaurant');
