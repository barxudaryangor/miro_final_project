CREATE TABLE submissions (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID         NOT NULL,
    student_id    UUID         NOT NULL,
    content       TEXT,
    submitted_at  TIMESTAMP    NOT NULL DEFAULT now(),
    grade         DECIMAL(5,2),
    status        VARCHAR(20)  NOT NULL DEFAULT 'SUBMITTED',
    UNIQUE (assignment_id, student_id)
);
