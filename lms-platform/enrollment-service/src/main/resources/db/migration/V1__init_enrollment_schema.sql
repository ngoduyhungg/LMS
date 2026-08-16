-- 1. Bảng lưu trữ số liệu đồng bộ từ Kafka
CREATE TABLE course_metrics (
                                course_id BIGINT PRIMARY KEY,
                                total_lessons INTEGER NOT NULL DEFAULT 0,
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. Bảng Enrollments
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

    -- Audit fields
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP WITH TIME ZONE,
                             created_by VARCHAR(255),
                             updated_by VARCHAR(255),

                             CONSTRAINT uk_enrollments_user_course UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);

-- 3. Bảng Lesson Progress (Child của Enrollments)
CREATE TABLE lesson_progress (
                                 id BIGSERIAL PRIMARY KEY,
                                 enrollment_id BIGINT NOT NULL,
                                 lesson_id BIGINT NOT NULL,
                                 status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
                                 watched_seconds INTEGER NOT NULL DEFAULT 0,
                                 completed_at TIMESTAMP WITH TIME ZONE,

                                 CONSTRAINT fk_lesson_progress_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
                                 CONSTRAINT uk_lesson_progress_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

CREATE INDEX idx_lesson_progress_lesson_id ON lesson_progress(lesson_id);

-- 4. Bảng Mẫu Chứng chỉ
CREATE TABLE certificates (
                              id BIGSERIAL PRIMARY KEY,
                              course_id BIGINT NOT NULL CONSTRAINT uk_certificates_course UNIQUE,
                              title VARCHAR(255) NOT NULL,
                              template_url VARCHAR(500) NOT NULL,

    -- Audit fields
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP WITH TIME ZONE,
                              created_by VARCHAR(255),
                              updated_by VARCHAR(255)
);

-- 5. Bảng Chứng chỉ đã cấp (Vĩnh viễn)
CREATE TABLE user_certificates (
                                   id BIGSERIAL PRIMARY KEY,
                                   certificate_id BIGINT NOT NULL,
                                   user_id VARCHAR(36) NOT NULL,
                                   enrollment_id BIGINT NOT NULL,
                                   certificate_code VARCHAR(100) NOT NULL,
                                   pdf_url VARCHAR(500) NOT NULL,
                                   issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

                                   CONSTRAINT fk_user_certificates_cert FOREIGN KEY (certificate_id) REFERENCES certificates(id) ON DELETE RESTRICT,
                                   CONSTRAINT fk_user_certificates_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE RESTRICT,
                                   CONSTRAINT uk_user_certificates_enrollment UNIQUE (enrollment_id),
                                   CONSTRAINT uk_user_certificates_code UNIQUE (certificate_code)
);

CREATE INDEX idx_user_certificates_user_id ON user_certificates(user_id);