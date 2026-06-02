-- V5: per-site SEO / analytics settings.
--
-- Stored as dedicated columns (not inside content_json) because these are
-- *not* template content — they are platform-level integration settings
-- whose lifecycle differs from the editable site content. Keeping them
-- separate also makes them queryable for analytics dashboards later.

ALTER TABLE user_sites
    ADD COLUMN gsc_verification  VARCHAR(128),
    ADD COLUMN ga_measurement_id VARCHAR(32);
