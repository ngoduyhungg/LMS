-- V1__init_enrollment_schema.sql

-- [BẢNG MỚI] Local Projection để lưu trữ tổng số bài học từ Kafka
CREATE TABLE course_metrics (
    course_id BIGINT PRIMARY KEY,
    total_lessons INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Bảng Enrollments (Đã gộp course_progress)
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    course_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    progress_percentage NUMERIC(5, 2) DEFAULT 0.00 NOT NULL,
    last_accessed_lesson_id BIGINT,
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_enrollments_user_course UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);

-- Bảng Lesson Progress
CREATE TABLE lesson_progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    watched_seconds INTEGER DEFAULT 0,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_lesson_progress_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    CONSTRAINT uk_lesson_progress_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

CREATE INDEX idx_lesson_progress_lesson_id ON lesson_progress(lesson_id);

-- Bảng Certificates (Mẫu chứng chỉ của khóa học)
CREATE TABLE certificates (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL CONSTRAINT uk_certificates_course UNIQUE,
    title VARCHAR(255) NOT NULL,
    template_url VARCHAR(500) NOT NULL
);

-- Bảng User Certificates (Chứng chỉ cấp vĩnh viễn)
CREATE TABLE user_certificates (
    id BIGSERIAL PRIMARY KEY,
    certificate_id BIGINT NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    enrollment_id BIGINT NOT NULL,
    certificate_code VARCHAR(100) NOT NULL,
    pdf_url VARCHAR(500) NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_certificates_cert FOREIGN KEY (certificate_id) REFERENCES certificates(id) ON DELETE RESTRICT,
    CONSTRAINT fk_user_certificates_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_certificates_enrollment UNIQUE (enrollment_id),
    CONSTRAINT uk_user_certificates_code UNIQUE (certificate_code)
);

CREATE INDEX idx_user_certificates_user_id ON user_certificates(user_id);