-- ==============================================================================
-- Flyway Migration: V2__insert_course_seed_data.sql
-- Description: Insert seed data exclusively for Course Microservice
-- ==============================================================================

-- 1. INSERT COURSES (instructor_id sử dụng dạng chuỗi UUID giả lập của Keycloak)
INSERT INTO courses (id, instructor_id, title, slug, description, thumbnail_url, price, status, created_at, updated_at)
VALUES
    (1, 'instructor-uuid-0002', 'Khóa học Spring Boot 4 Thực chiến cho Backend Developer', 'spring-boot-4-thuc-chien', 'Làm chủ Spring Boot 4, Spring Security 6, JPA/Hibernate và Xây dựng RESTful API Enterprise-grade chuẩn Production.', 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80', 1299000.00, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'instructor-uuid-0002', 'React 18 & TypeScript Masterclass: Từ Zero đến Hero', 'react-18-typescript-masterclass', 'Khóa học toàn diện về React 18, Hooks chuyên sâu, Redux Toolkit, React Query và tích hợp TypeScript đỉnh cao.', 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?auto=format&fit=crop&w=800&q=80', 1499000.00, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. INSERT MODULES
INSERT INTO modules (id, course_id, title, sort_order, created_at, updated_at)
VALUES
    (1, 1, 'Chương 1: Khởi tạo dự án & Kiến trúc Spring Boot 4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Chương 2: Spring Data JPA & PostgreSQL Chuyên sâu', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 'Chương 1: Làm quen với TypeScript trong Ecosystem React', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. INSERT LESSONS
INSERT INTO lessons (id, module_id, title, lesson_type, video_url, content, sort_order, duration_seconds, is_preview, created_at, updated_at)
VALUES
    (1, 1, 'Bài 1.1: Giới thiệu hệ sinh thái Spring Boot 4 & Java 21', 'VIDEO', 'https://www.youtube.com/embed/9SGDpanrc8U', NULL, 1, 1500, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Bài 1.2: Cài đặt môi trường development (IntelliJ IDEA, Docker, Postman)', 'TEXT', NULL, '### Hướng dẫn cài đặt\n1. Cài đặt **JDK 21** (Eclipse Temurin).\n2. Cài đặt **Docker Desktop** để chạy PostgreSQL.\n3. Cài đặt plugin *Lombok* và *Spring Boot Helper* trên IntelliJ.', 2, 900, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 'Bài 1.3: Hiểu sâu về IoC Container và Dependency Injection (DI)', 'VIDEO', 'https://www.youtube.com/embed/3aGzT3k1vGE', NULL, 3, 2100, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 2, 'Bài 2.1: Thiết kế Database Schema cho hệ thống lớn với Flyway', 'VIDEO', 'https://www.youtube.com/embed/6e4t3v9n5hM', NULL, 1, 2400, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 3, 'Bài 1.1: Tại sao phải sử dụng TypeScript với React 18?', 'VIDEO', 'https://www.youtube.com/embed/SqcY0GlETPk', NULL, 1, 1800, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==============================================================================
-- RESET POSTGRESQL SEQUENCES
-- ==============================================================================
SELECT setval('courses_id_seq', (SELECT MAX(id) FROM courses));
SELECT setval('modules_id_seq', (SELECT MAX(id) FROM modules));
SELECT setval('lessons_id_seq', (SELECT MAX(id) FROM lessons));