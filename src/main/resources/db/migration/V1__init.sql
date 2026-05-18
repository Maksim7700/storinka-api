-- Storinka — initial schema (V1)
-- Mirrors ТД v3, section 3. Payment-related fields & tables are deferred
-- to a later migration (Vn) once the payment provider is chosen.

CREATE TABLE users (
    id             BIGSERIAL    PRIMARY KEY,
    email          VARCHAR(255) UNIQUE NOT NULL,
    password       VARCHAR(255),
    full_name      VARCHAR(255),
    phone          VARCHAR(32),
    avatar_url     VARCHAR(512),
    role           VARCHAR(32)  NOT NULL DEFAULT 'USER',
    plan           VARCHAR(32)  NOT NULL DEFAULT 'TRIAL',
    status         VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN      NOT NULL DEFAULT false,
    google_id      VARCHAR(128) UNIQUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE templates (
    id            BIGSERIAL      PRIMARY KEY,
    key           VARCHAR(64)    UNIQUE NOT NULL,
    name          VARCHAR(128)   NOT NULL,
    description   TEXT,
    thumbnail_url VARCHAR(512),
    category      VARCHAR(64),
    schema_json   JSONB          NOT NULL DEFAULT '{}',
    license_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    monthly_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    is_active     BOOLEAN        NOT NULL DEFAULT true,
    created_by    BIGINT         REFERENCES users(id),
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE user_sites (
    id            BIGSERIAL   PRIMARY KEY,
    user_id       BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    template_id   BIGINT      NOT NULL REFERENCES templates(id),
    subdomain     VARCHAR(63) UNIQUE NOT NULL,
    content_json  JSONB       NOT NULL DEFAULT '{}',
    custom_domain VARCHAR(255),
    status        VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
