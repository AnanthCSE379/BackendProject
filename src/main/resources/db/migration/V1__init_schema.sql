CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users (id) ON DELETE CASCADE,
    token VARCHAR(512) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    student_code VARCHAR(40) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),
    date_of_birth DATE,
    gender VARCHAR(30),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    emergency_contact_name VARCHAR(120),
    emergency_contact_phone VARCHAR(30),
    emergency_contact_relation VARCHAR(80),
    course_name VARCHAR(120) NOT NULL,
    major VARCHAR(120),
    academic_year SMALLINT NOT NULL,
    enrollment_date DATE NOT NULL,
    expected_graduation_date DATE,
    gpa NUMERIC(3,2),
    credits_completed INTEGER,
    status VARCHAR(30) NOT NULL,
    notes VARCHAR(1500),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_students_academic_year CHECK (academic_year >= 1 AND academic_year <= 10),
    CONSTRAINT chk_students_gpa CHECK (gpa IS NULL OR (gpa >= 0.00 AND gpa <= 4.00))
);

CREATE INDEX idx_students_email ON students (email);
CREATE INDEX idx_students_code ON students (student_code);
CREATE INDEX idx_students_course_name ON students (course_name);
CREATE INDEX idx_students_status ON students (status);
CREATE INDEX idx_students_enrollment_date ON students (enrollment_date);
CREATE INDEX idx_students_name ON students (first_name, last_name);
