CREATE TABLE links (
                       id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       full_url    TEXT        NOT NULL,
                       short_code  VARCHAR(8)  NOT NULL UNIQUE,
                       created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
                       expires_at  TIMESTAMP   NOT NULL
);