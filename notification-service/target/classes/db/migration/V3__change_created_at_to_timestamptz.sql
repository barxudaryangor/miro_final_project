ALTER TABLE notification_logs
ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE notification_logs
    ALTER COLUMN created_at DROP DEFAULT;