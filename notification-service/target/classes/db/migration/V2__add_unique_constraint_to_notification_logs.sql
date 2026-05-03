ALTER TABLE notification_logs
    ADD CONSTRAINT uq_notification_logs_event_id UNIQUE (event_id);