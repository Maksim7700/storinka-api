-- V2: seed "Салон краси" template (final state — fields + tags + features).

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
     {"key": "businessName", "label": "Назва салону",  "type": "string",   "required": true,  "placeholder": "Lumina Salon"},
     {"key": "tagline",      "label": "Слоган",        "type": "string",   "required": false, "placeholder": "Краса в кожній деталі"},
     {"key": "description",  "label": "Опис (для пошуку та превʼю)", "type": "textarea", "required": false, "placeholder": "1-2 речення про салон. 120-160 символів. Що пропонуєте, чим відрізняєтесь."},
     {"key": "phone",        "label": "Телефон",       "type": "string",   "required": true,  "placeholder": "+380..."},
     {"key": "address",      "label": "Адреса",        "type": "string",   "required": false},
     {"key": "primaryColor", "label": "Основний колір","type": "color",    "required": false, "default": "#6366f1"}
   ],
   "tags": ["ОНЛАЙН ЗАПИС"],
   "features": [
     {"label": "Безкоштовний домен",   "icon": "signal"},
     {"label": "Безкоштовний хостинг", "icon": "signal"},
     {"label": "Адаптивний дизайн",    "icon": "signal"},
     {"label": "Хороше SEO",           "icon": "signal"},
     {"label": "Галерея робіт",        "icon": "file"},
     {"label": "Форма онлайн запису",  "icon": "file"}
   ]
 }'::jsonb,
 3990.00, 990.00, true);
