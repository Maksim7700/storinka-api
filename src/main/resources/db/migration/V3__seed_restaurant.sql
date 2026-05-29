-- V3: seed "Ресторан" template (final state — fields + tags + features).

INSERT INTO templates (key, name, description, thumbnail_url, category,
                       schema_json, license_price, monthly_price, is_active)
VALUES
('restaurant',
 'Ресторан',
 'Лендінг для ресторану з меню, галереєю і бронюванням столиків.',
 'https://placehold.co/640x400/dc2626/ffffff?text=Restaurant',
 'food',
 '{
   "fields": [
     {"key": "businessName", "label": "Назва ресторану", "type": "string",   "required": true,  "placeholder": "Pizza Roma"},
     {"key": "cuisine",      "label": "Кухня",           "type": "string",   "required": false, "placeholder": "Італійська"},
     {"key": "description",  "label": "Опис (для пошуку та превʼю)", "type": "textarea", "required": false, "placeholder": "1-2 речення про ресторан. 120-160 символів. Кухня, атмосфера, фішки."},
     {"key": "phone",        "label": "Телефон",         "type": "string",   "required": true},
     {"key": "address",      "label": "Адреса",          "type": "string",   "required": false}
   ],
   "tags": ["БРОНЮВАННЯ"],
   "features": [
     {"label": "Безкоштовний домен",   "icon": "signal"},
     {"label": "Безкоштовний хостинг", "icon": "signal"},
     {"label": "Адаптивний дизайн",    "icon": "signal"},
     {"label": "Хороше SEO",           "icon": "signal"},
     {"label": "Меню зі стравами",     "icon": "file"},
     {"label": "Форма бронювання",     "icon": "file"}
   ]
 }'::jsonb,
 3990.00, 990.00, true);
