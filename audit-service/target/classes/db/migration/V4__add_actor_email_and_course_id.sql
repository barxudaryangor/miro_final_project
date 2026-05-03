ALTER TABLE audit_logs
    ADD COLUMN actor_email VARCHAR(255),
ADD COLUMN course_id UUID;