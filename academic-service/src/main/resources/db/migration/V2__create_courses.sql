CREATE TABLE courses (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title        VARCHAR(255) NOT NULL,
    credits      INT,
    professor_id UUID         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE student_courses (
    id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID      NOT NULL,
    course_id  UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (student_id, course_id)
);
