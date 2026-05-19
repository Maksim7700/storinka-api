-- Enrich seed templates with tags + features arrays.
-- `fields` (form definition) is preserved by the JSON concatenation operator.

UPDATE templates
SET schema_json = schema_json
    || jsonb_build_object(
           'tags', jsonb_build_array('ОНЛАЙН ЗАПИС'),
           'features', jsonb_build_array(
               jsonb_build_object('label', 'Безкоштовний домен',   'icon', 'signal'),
               jsonb_build_object('label', 'Безкоштовний хостинг', 'icon', 'signal'),
               jsonb_build_object('label', 'Адаптивний дизайн',    'icon', 'signal'),
               jsonb_build_object('label', 'Хороше SEO',           'icon', 'signal'),
               jsonb_build_object('label', 'Галерея робіт',        'icon', 'file'),
               jsonb_build_object('label', 'Форма онлайн запису',  'icon', 'file')
           )
       )
WHERE key = 'beauty-salon';

UPDATE templates
SET schema_json = schema_json
    || jsonb_build_object(
           'tags', jsonb_build_array('БРОНЮВАННЯ'),
           'features', jsonb_build_array(
               jsonb_build_object('label', 'Безкоштовний домен',   'icon', 'signal'),
               jsonb_build_object('label', 'Безкоштовний хостинг', 'icon', 'signal'),
               jsonb_build_object('label', 'Адаптивний дизайн',    'icon', 'signal'),
               jsonb_build_object('label', 'Хороше SEO',           'icon', 'signal'),
               jsonb_build_object('label', 'Меню зі стравами',     'icon', 'file'),
               jsonb_build_object('label', 'Форма бронювання',     'icon', 'file')
           )
       )
WHERE key = 'restaurant';
