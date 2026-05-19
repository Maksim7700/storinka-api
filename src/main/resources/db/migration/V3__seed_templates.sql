-- Storinka — V3: seed two example templates so /catalog has content to show
-- in development before MANAGER UI exists.

INSERT INTO templates (key, name, description, thumbnail_url, category,
                       schema_json, license_price, monthly_price, is_active)
VALUES
('beauty-salon',
 'Салон краси',
 'Сучасний лендінг для салону краси з галереєю робіт та формою запису.',
 'https://placehold.co/640x400/6366f1/ffffff?text=Beauty+Salon',
 'beauty',
 '{
   "fields": [
     {"key": "businessName", "label": "Назва салону", "type": "string", "required": true, "placeholder": "Lumina Salon"},
     {"key": "tagline", "label": "Слоган", "type": "string", "required": false, "placeholder": "Краса в кожній деталі"},
     {"key": "phone", "label": "Телефон", "type": "string", "required": true, "placeholder": "+380..."},
     {"key": "address", "label": "Адреса", "type": "string", "required": false},
     {"key": "primaryColor", "label": "Основний колір", "type": "color", "required": false, "default": "#6366f1"}
   ]
 }'::jsonb,
 990.00, 990.00, true),

('restaurant',
 'Ресторан',
 'Лендінг для ресторану з меню, галереєю і бронюванням столиків.',
 'https://placehold.co/640x400/dc2626/ffffff?text=Restaurant',
 'food',
 '{
   "fields": [
     {"key": "businessName", "label": "Назва ресторану", "type": "string", "required": true, "placeholder": "Pizza Roma"},
     {"key": "cuisine", "label": "Кухня", "type": "string", "required": false, "placeholder": "Італійська"},
     {"key": "phone", "label": "Телефон", "type": "string", "required": true},
     {"key": "address", "label": "Адреса", "type": "string", "required": false}
   ]
 }'::jsonb,
 990.00, 990.00, true);
