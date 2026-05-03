CREATE TABLE assignments (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    due_date     TIMESTAMP,
    course_id    UUID         NOT NULL,
    professor_id UUID         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT now()
);
