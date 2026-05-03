CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE notification_logs (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id        UUID         NOT NULL,
    recipient_email VARCHAR(255),
    type            VARCHAR(100),
    message         TEXT,
    status          VARCHAR(20)  NOT NULL DEFAULT 'SENT',
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);
