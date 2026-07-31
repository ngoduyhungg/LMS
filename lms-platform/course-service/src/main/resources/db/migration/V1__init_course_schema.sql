-- ============================================================================
-- MICROSERVICE: COURSE SERVICE (Spring Boot 4, PostgreSQL, Flyway)
-- VERSION: V1__init_course_schema.sql
-- DESCRIPTION: Independent database schema for Course, Module, and Lesson
-- ============================================================================

-- 1. CATEGORIES TABLE
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT CONSTRAINT fk_categories_parent REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL CONSTRAINT uk_categories_slug UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. COURSES TABLE (instructor_id is now VARCHAR without FK to users)
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT CONSTRAINT fk_courses_category REFERENCES categories(id) ON DELETE SET NULL,
    instructor_id VARCHAR(100) NOT NULL, -- Keycloak Subject UUID
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

-- 3. MODULES TABLE
CREATE TABLE modules (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL CONSTRAINT fk_modules_course REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    sort_order INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 4. LESSONS TABLE
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

-- 5. LESSON RESOURCES TABLE
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
-- HIGH-PERFORMANCE INDEXES
-- ============================================================================
CREATE INDEX idx_courses_category_id ON courses(category_id);
CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_modules_course_id_sort ON modules(course_id, sort_order);
CREATE INDEX idx_lessons_module_id_sort ON lessons(module_id, sort_order);
CREATE INDEX idx_lesson_resources_lesson_id ON lesson_resources(lesson_id);