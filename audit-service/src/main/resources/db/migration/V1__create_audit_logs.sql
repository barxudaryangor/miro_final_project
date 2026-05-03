CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE audit_logs (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id     UUID         NOT NULL UNIQUE,
    event_type   VARCHAR(100) NOT NULL,
    actor_id     UUID,
    actor_role   VARCHAR(50),
    object_type  VARCHAR(100),
    object_id    UUID,
    action       VARCHAR(255),
    details_json TEXT,
    ip_address   VARCHAR(50),
    created_at   TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
