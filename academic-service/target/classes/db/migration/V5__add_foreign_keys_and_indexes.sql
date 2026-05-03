ALTER TABLE courses
    ADD CONSTRAINT fk_courses_professor
        FOREIGN KEY (professor_id)
            REFERENCES professors(id);

ALTER TABLE student_courses
    ADD CONSTRAINT fk_student_courses_student
        FOREIGN KEY (student_id)
            REFERENCES students(id);

ALTER TABLE student_courses
    ADD CONSTRAINT fk_student_courses_course
        FOREIGN KEY (course_id)
            REFERENCES courses(id);

ALTER TABLE assignments
    ADD CONSTRAINT fk_assignments_course
        FOREIGN KEY (course_id)
            REFERENCES courses(id);

ALTER TABLE assignments
    ADD CONSTRAINT fk_assignments_professor
        FOREIGN KEY (professor_id)
            REFERENCES professors(id);

ALTER TABLE submissions
    ADD CONSTRAINT fk_submissions_assignment
        FOREIGN KEY (assignment_id)
            REFERENCES assignments(id);

ALTER TABLE submissions
    ADD CONSTRAINT fk_submissions_student
        FOREIGN KEY (student_id)
            REFERENCES students(id);



CREATE INDEX idx_courses_professor_id
    ON courses(professor_id);

CREATE INDEX idx_student_courses_student_id
    ON student_courses(student_id);

CREATE INDEX idx_student_courses_course_id
    ON student_courses(course_id);

CREATE INDEX idx_assignments_course_id
    ON assignments(course_id);

CREATE INDEX idx_assignments_professor_id
    ON assignments(professor_id);

CREATE INDEX idx_submissions_assignment_id
    ON submissions(assignment_id);

CREATE INDEX idx_submissions_student_id
    ON submissions(student_id);