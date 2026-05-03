CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE students (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID,
    name        VARCHAR(100) NOT NULL,
    surname     VARCHAR(100) NOT NULL,
    gender      VARCHAR(20),
    birth_date  DATE,
    email       VARCHAR(255) UNIQUE NOT NULL,
    enroll_date DATE,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE professors (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID,
    name       VARCHAR(100) NOT NULL,
    surname    VARCHAR(100) NOT NULL,
    department VARCHAR(255),
    email      VARCHAR(255) UNIQUE NOT NULL
);
