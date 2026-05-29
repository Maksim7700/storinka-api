-- V4: seed "СТО" (auto service station) template.

INSERT INTO templates (key, name, description, thumbnail_url, category,
                       schema_json, license_price, monthly_price, is_active)
VALUES
('sto',
 'СТО',
 'Лендінг для станції технічного обслуговування з переліком послуг, контактами та картою.',
 'https://placehold.co/640x400/0f172a/ffffff?text=%D0%A1%D0%A2%D0%9E',
 'auto',
 '{
   "fields": [
     {"key": "businessName", "label": "Назва СТО",                         "type": "string",   "required": true,  "placeholder": "Авто-Майстер"},
     {"key": "description",  "label": "Опис (для пошуку та превʼю)",       "type": "textarea", "required": false, "placeholder": "1-2 речення про СТО. 120-160 символів. Послуги, район, чим відрізняєтесь."},
     {"key": "photo",        "label": "Фото для головної",                 "type": "image",   "required": false},
     {"key": "services",     "label": "Список послуг (по одній на рядок)", "type": "string",  "required": true,  "placeholder": "Заміна оливи; Діагностика; Ремонт ходової"},
     {"key": "phone",        "label": "Номер телефону",                    "type": "string", "required": true,  "placeholder": "+380..."},
     {"key": "map",          "label": "Адреса (для карти)",                "type": "string", "required": false, "placeholder": "Київ, вул. Автомеханічна 5"},
     {"key": "email",        "label": "Email",                             "type": "string", "required": true,  "placeholder": "info@sto.ua"}
   ],
   "tags": ["ОНЛАЙН ЗАПИС"],
   "features": [
     {"label": "Безкоштовний домен",   "icon": "signal"},
     {"label": "Безкоштовний хостинг", "icon": "signal"},
     {"label": "Адаптивний дизайн",    "icon": "signal"},
     {"label": "Хороше SEO",           "icon": "signal"},
     {"label": "Список послуг",        "icon": "file"},
     {"label": "Карта та контакти",    "icon": "file"}
   ]
 }'::jsonb,
 3990.00, 990.00, true);
