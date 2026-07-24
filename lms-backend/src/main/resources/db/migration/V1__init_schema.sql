-- ============================================================================
-- PROJECT: Fullstack LMS (Spring Boot 4, PostgreSQL, Flyway)
-- VERSION: V1__init_schema.sql
-- DESCRIPTION: Initial database schema creation (3NF, Enterprise Standard)
-- ============================================================================

-- Enable UUID extension if needed for public external IDs (optional utility)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- MODULE 1: AUTH / RBAC
-- ============================================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL CONSTRAINT uk_users_email UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    avatar_url VARCHAR(500),
    phone_number VARCHAR(20),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED', 'PENDING_VERIFICATION')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL CONSTRAINT uk_roles_name UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL CONSTRAINT uk_permissions_name UNIQUE,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL CONSTRAINT fk_user_roles_user REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL CONSTRAINT fk_user_roles_role REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL CONSTRAINT fk_role_permissions_role REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL CONSTRAINT fk_role_permissions_permission REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT fk_refresh_tokens_user REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL CONSTRAINT uk_refresh_tokens_token UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ============================================================================
-- MODULE 2: COURSE / LESSON
-- ============================================================================

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT CONSTRAINT fk_categories_parent REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL CONSTRAINT uk_categories_slug UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT CONSTRAINT fk_courses_category REFERENCES categories(id) ON DELETE SET NULL,
    instructor_id BIGINT NOT NULL CONSTRAINT fk_courses_instructor REFERENCES users(id) ON DELETE RESTRICT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(280) NOT NULL CONSTRAINT uk_courses_slug UNIQUE,
    summary VARCHAR(500),
    description TEXT,
    thumbnail_url VARCHAR(500),
    price NUMERIC(12, 2) DEFAULT 0.00 NOT NULL CONSTRAINT chk_courses_price CHECK (price >= 0),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' CONSTRAINT chk_courses_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'SUSPENDED')),
    level VARCHAR(30) DEFAULT 'BEGINNER' CONSTRAINT chk_courses_level CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALL_LEVELS')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE modules (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL CONSTRAINT fk_modules_course REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    sort_order INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE lessons (
    id BIGSERIAL PRIMARY KEY,
    module_id BIGINT NOT NULL CONSTRAINT fk_lessons_module REFERENCES modules(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    video_url VARCHAR(500),
    duration_seconds INTEGER DEFAULT 0 CONSTRAINT chk_lessons_duration CHECK (duration_seconds >= 0),
    lesson_type VARCHAR(30) NOT NULL DEFAULT 'VIDEO' CONSTRAINT chk_lessons_type CHECK (lesson_type IN ('VIDEO', 'TEXT', 'INTERACTIVE', 'LIVE_STREAM')),
    is_preview BOOLEAN DEFAULT FALSE NOT NULL,
    sort_order INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE lesson_resources (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL CONSTRAINT fk_lesson_resources_lesson REFERENCES lessons(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size_bytes BIGINT CONSTRAINT chk_lesson_resources_size CHECK (file_size_bytes >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ============================================================================
-- MODULE 3: ENROLLMENT / PROGRESS
-- ============================================================================

CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT fk_enrollments_user REFERENCES users(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL CONSTRAINT fk_enrollments_course REFERENCES courses(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' CONSTRAINT chk_enrollments_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED')),
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_enrollments_user_course UNIQUE (user_id, course_id)
);

CREATE TABLE course_progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL CONSTRAINT fk_course_progress_enrollment REFERENCES enrollments(id) ON DELETE CASCADE CONSTRAINT uk_course_progress_enrollment UNIQUE,
    progress_percentage NUMERIC(5, 2) DEFAULT 0.00 NOT NULL CONSTRAINT chk_course_progress_pct CHECK (progress_percentage >= 0.00 AND progress_percentage <= 100.00),
    last_accessed_lesson_id BIGINT CONSTRAINT fk_course_progress_lesson REFERENCES lessons(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE lesson_progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL CONSTRAINT fk_lesson_progress_enrollment REFERENCES enrollments(id) ON DELETE CASCADE,
    lesson_id BIGINT NOT NULL CONSTRAINT fk_lesson_progress_lesson REFERENCES lessons(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS' CONSTRAINT chk_lesson_progress_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED')),
    watched_seconds INTEGER DEFAULT 0 CONSTRAINT chk_lesson_progress_watched CHECK (watched_seconds >= 0),
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_lesson_progress_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

-- ============================================================================
-- MODULE 4: QUIZ / ASSESSMENT
-- ============================================================================

CREATE TABLE quizzes (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL CONSTRAINT fk_quizzes_course REFERENCES courses(id) ON DELETE CASCADE,
    lesson_id BIGINT CONSTRAINT fk_quizzes_lesson REFERENCES lessons(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    time_limit_minutes INTEGER CONSTRAINT chk_quizzes_time_limit CHECK (time_limit_minutes > 0),
    passing_score NUMERIC(5, 2) DEFAULT 70.00 NOT NULL CONSTRAINT chk_quizzes_passing_score CHECK (passing_score >= 0.00 AND passing_score <= 100.00),
    max_attempts INTEGER DEFAULT 3 CONSTRAINT chk_quizzes_max_attempts CHECK (max_attempts > 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL CONSTRAINT fk_questions_quiz REFERENCES quizzes(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    question_type VARCHAR(30) NOT NULL DEFAULT 'SINGLE_CHOICE' CONSTRAINT chk_questions_type CHECK (question_type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER')),
    points NUMERIC(5, 2) DEFAULT 1.00 NOT NULL CONSTRAINT chk_questions_points CHECK (points > 0),
    sort_order INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE question_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL CONSTRAINT fk_question_options_question REFERENCES questions(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE NOT NULL,
    explanation TEXT,
    sort_order INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE quiz_submissions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL CONSTRAINT fk_quiz_submissions_quiz REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL CONSTRAINT fk_quiz_submissions_user REFERENCES users(id) ON DELETE CASCADE,
    enrollment_id BIGINT NOT NULL CONSTRAINT fk_quiz_submissions_enrollment REFERENCES enrollments(id) ON DELETE CASCADE,
    attempt_number INTEGER NOT NULL CONSTRAINT chk_quiz_submissions_attempt CHECK (attempt_number > 0),
    total_score NUMERIC(5, 2) DEFAULT 0.00 NOT NULL CONSTRAINT chk_quiz_submissions_score CHECK (total_score >= 0.00),
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS' CONSTRAINT chk_quiz_submissions_status CHECK (status IN ('IN_PROGRESS', 'SUBMITTED', 'GRADED', 'TIMED_OUT')),
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    submitted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_quiz_submissions_attempt UNIQUE (quiz_id, user_id, attempt_number)
);

CREATE TABLE submission_answers (
    id BIGSERIAL PRIMARY KEY,
    submission_id BIGINT NOT NULL CONSTRAINT fk_submission_answers_submission REFERENCES quiz_submissions(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL CONSTRAINT fk_submission_answers_question REFERENCES questions(id) ON DELETE RESTRICT,
    selected_option_id BIGINT CONSTRAINT fk_submission_answers_option REFERENCES question_options(id) ON DELETE SET NULL,
    answer_text TEXT,
    score_earned NUMERIC(5, 2) DEFAULT 0.00 NOT NULL CONSTRAINT chk_submission_answers_score CHECK (score_earned >= 0.00),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ============================================================================
-- MODULE 5: ATTENDANCE / NOTIFICATION
-- ============================================================================

CREATE TABLE attendance_sessions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL CONSTRAINT fk_attendance_sessions_course REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    check_in_code VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL CONSTRAINT fk_attendance_records_session REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL CONSTRAINT fk_attendance_records_user REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'PRESENT' CONSTRAINT chk_attendance_records_status CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),
    check_in_time TIMESTAMP WITH TIME ZONE,
    note VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_attendance_records_session_user UNIQUE (session_id, user_id)
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT CONSTRAINT fk_notifications_sender REFERENCES users(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL' CONSTRAINT chk_notifications_type CHECK (notification_type IN ('GENERAL', 'COURSE_ANNOUNCEMENT', 'QUIZ_REMINDER', 'SYSTEM', 'CERTIFICATE')),
    reference_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE user_notifications (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL CONSTRAINT fk_user_notifications_notif REFERENCES notifications(id) ON DELETE CASCADE,
    recipient_id BIGINT NOT NULL CONSTRAINT fk_user_notifications_recipient REFERENCES users(id) ON DELETE CASCADE,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_notifications_recipient_notif UNIQUE (recipient_id, notification_id)
);

-- ============================================================================
-- MODULE 6: CERTIFICATE
-- ============================================================================

CREATE TABLE certificates (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL CONSTRAINT fk_certificates_course REFERENCES courses(id) ON DELETE CASCADE CONSTRAINT uk_certificates_course UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    template_url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE user_certificates (
    id BIGSERIAL PRIMARY KEY,
    certificate_id BIGINT NOT NULL CONSTRAINT fk_user_certificates_cert REFERENCES certificates(id) ON DELETE RESTRICT,
    user_id BIGINT NOT NULL CONSTRAINT fk_user_certificates_user REFERENCES users(id) ON DELETE CASCADE,
    enrollment_id BIGINT NOT NULL CONSTRAINT fk_user_certificates_enrollment REFERENCES enrollments(id) ON DELETE CASCADE CONSTRAINT uk_user_certificates_enrollment UNIQUE,
    certificate_code VARCHAR(100) NOT NULL CONSTRAINT uk_user_certificates_code UNIQUE,
    pdf_url VARCHAR(500) NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_certificates_user_cert UNIQUE (user_id, certificate_id)
);

-- ============================================================================
-- HIGH-PERFORMANCE INDEXES (For Spring Boot / JPA Joins & Queries)
-- ============================================================================

-- Auth & Users
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Courses & Curriculum
CREATE INDEX idx_courses_category_id ON courses(category_id);
CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_modules_course_id_sort ON modules(course_id, sort_order);
CREATE INDEX idx_lessons_module_id_sort ON lessons(module_id, sort_order);
CREATE INDEX idx_lesson_resources_lesson_id ON lesson_resources(lesson_id);

-- Enrollments & Progress
CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_lesson_progress_enrollment_id ON lesson_progress(enrollment_id);
CREATE INDEX idx_lesson_progress_status ON lesson_progress(status);

-- Quizzes & Assessments
CREATE INDEX idx_quizzes_course_id ON quizzes(course_id);
CREATE INDEX idx_quizzes_lesson_id ON quizzes(lesson_id);
CREATE INDEX idx_questions_quiz_id_sort ON questions(quiz_id, sort_order);
CREATE INDEX idx_question_options_question_id ON question_options(question_id);
CREATE INDEX idx_quiz_submissions_user_id ON quiz_submissions(user_id);
CREATE INDEX idx_quiz_submissions_enrollment_id ON quiz_submissions(enrollment_id);
CREATE INDEX idx_submission_answers_submission_id ON submission_answers(submission_id);

-- Attendance & Notifications
CREATE INDEX idx_attendance_sessions_course_date ON attendance_sessions(course_id, session_date);
CREATE INDEX idx_attendance_records_user_id ON attendance_records(user_id);
CREATE INDEX idx_user_notifications_recipient_read ON user_notifications(recipient_id, is_read);

-- Certificates
CREATE INDEX idx_user_certificates_user_id ON user_certificates(user_id);