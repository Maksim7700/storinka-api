-- V6: switch template thumbnails from placehold.co to real screenshots.
--
-- Files live under frontend/public/templates/<key>.png and are served by Next
-- at /templates/<key>.png on the platform host. We keep the URLs relative
-- (no scheme/host) so dev and prod work without env-specific tweaks.
--
-- If you re-shoot a screenshot, just replace the file under public/templates/
-- and clients will see the new image on the next request — no DB change.

UPDATE templates SET thumbnail_url = '/templates/beauty-salon.png'
WHERE key = 'beauty-salon';

UPDATE templates SET thumbnail_url = '/templates/restaurant.png'
WHERE key = 'restaurant';

UPDATE templates SET thumbnail_url = '/templates/sto.png'
WHERE key = 'sto';
