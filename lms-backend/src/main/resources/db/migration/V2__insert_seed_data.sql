-- ==============================================================================
-- Flyway Migration: V2__insert_seed_data.sql
-- Description: Insert rich seed data for LMS (Users, Courses, Modules, Lessons, Quiz, Attendance)
-- ==============================================================================

-- 0. INSERT ROLES
INSERT INTO roles (id, name, description, created_at, updated_at)
VALUES
    (1, 'ADMIN', 'Quản Trị Viên Hệ Thống', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'INSTRUCTOR', 'Giảng Viên / Gia Sư', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'STUDENT', 'Học Viên', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- 1. INSERT USERS (Mật khẩu mặc định: '123456' -> $2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUK)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
VALUES
    (1, 'admin@lms-system.edu.vn', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUK', 'Quản Trị Viên Hệ Thống', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'instructor@lms-system.edu.vn', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUK', 'ThS. Nguyễn Văn Giảng Viên', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'student.john@gmail.com', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUK', 'Trần Học Viên', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'alice.nguyen@gmail.com', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUK', 'Nguyễn Thị Alice', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'bob.le@gmail.com', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUK', 'Lê Văn Bob', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1), (2, 2), (3, 3), (4, 3), (5, 3);

-- 2. INSERT COURSES
INSERT INTO courses (id, instructor_id, title, slug, description, thumbnail_url, price, status, created_at, updated_at)
VALUES
    (1, 2, 'Khóa học Spring Boot 4 Thực chiến cho Backend Developer', 'spring-boot-4-thuc-chien', 'Làm chủ Spring Boot 4, Spring Security 6, JPA/Hibernate và Xây dựng RESTful API Enterprise-grade chuẩn Production.', 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80', 1299000.00, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, 'React 18 & TypeScript Masterclass: Từ Zero đến Hero', 'react-18-typescript-masterclass', 'Khóa học toàn diện về React 18, Hooks chuyên sâu, Redux Toolkit, React Query và tích hợp TypeScript đỉnh cao.', 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?auto=format&fit=crop&w=800&q=80', 1499000.00, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. INSERT MODULES (Sử dụng sort_order thay vì order_index)
INSERT INTO modules (id, course_id, title, sort_order, created_at, updated_at)
VALUES
    (1, 1, 'Chương 1: Khởi tạo dự án & Kiến trúc Spring Boot 4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Chương 2: Spring Data JPA & PostgreSQL Chuyên sâu', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 'Chương 1: Làm quen với TypeScript trong Ecosystem React', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. INSERT LESSONS (Sử dụng video_url, content, duration_seconds, sort_order, is_preview)
INSERT INTO lessons (id, module_id, title, lesson_type, video_url, content, sort_order, duration_seconds, is_preview, created_at, updated_at)
VALUES
    (1, 1, 'Bài 1.1: Giới thiệu hệ sinh thái Spring Boot 4 & Java 21', 'VIDEO', 'https://www.youtube.com/embed/9SGDpanrc8U', NULL, 1, 1500, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Bài 1.2: Cài đặt môi trường development (IntelliJ IDEA, Docker, Postman)', 'TEXT', NULL, '### Hướng dẫn cài đặt\n1. Cài đặt **JDK 21** (Eclipse Temurin).\n2. Cài đặt **Docker Desktop** để chạy PostgreSQL.\n3. Cài đặt plugin *Lombok* và *Spring Boot Helper* trên IntelliJ.', 2, 900, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 'Bài 1.3: Hiểu sâu về IoC Container và Dependency Injection (DI)', 'VIDEO', 'https://www.youtube.com/embed/3aGzT3k1vGE', NULL, 3, 2100, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 2, 'Bài 2.1: Thiết kế Database Schema cho hệ thống lớn với Flyway', 'VIDEO', 'https://www.youtube.com/embed/6e4t3v9n5hM', NULL, 1, 2400, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 3, 'Bài 1.1: Tại sao phải sử dụng TypeScript với React 18?', 'VIDEO', 'https://www.youtube.com/embed/SqcY0GlETPk', NULL, 1, 1800, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. INSERT QUIZZES
INSERT INTO quizzes (id, course_id, lesson_id, title, description, time_limit_minutes, passing_score, created_at, updated_at)
VALUES
    (1, 1, 3, 'Quiz: Kiểm tra kiến thức về IoC và Dependency Injection', 'Bộ câu hỏi trắc nghiệm ôn tập kiến thức cốt lõi của Spring Framework. Bạn cần đạt tối thiểu 70% để đạt yêu cầu.', 15, 70.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. INSERT QUESTIONS (Tên bảng chuẩn là questions, dùng sort_order)
INSERT INTO questions (id, quiz_id, content, question_type, points, sort_order, created_at, updated_at)
VALUES
    (1, 1, 'IoC trong Spring Framework là viết tắt của cụm từ nào?', 'SINGLE_CHOICE', 10.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Annotation nào sau đây được sử dụng để tiêm (inject) một dependency vào Bean trong Spring Boot?', 'SINGLE_CHOICE', 10.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 'Scope mặc định của một Spring Bean là gì?', 'SINGLE_CHOICE', 10.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. INSERT QUESTION OPTIONS (Tên bảng chuẩn là question_options, cột content và is_correct)
INSERT INTO question_options (id, question_id, content, is_correct, sort_order, created_at, updated_at)
VALUES
    (1, 1, 'Inversion of Control', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Integration of Components', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 'Interface of Classes', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 1, 'Input Output Controller', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 2, '@Autowired', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 2, '@InjectBean', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 2, '@Component', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 2, '@BeanInject', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 3, 'Singleton', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 3, 'Prototype', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (11, 3, 'Request', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (12, 3, 'Session', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. INSERT ENROLLMENTS (Tên bảng chuẩn là enrollments)
INSERT INTO enrollments (id, course_id, user_id, enrolled_at, status, created_at, updated_at)
VALUES
    (1, 1, 3, CURRENT_TIMESTAMP - INTERVAL '10 days', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 4, CURRENT_TIMESTAMP - INTERVAL '9 days', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 5, CURRENT_TIMESTAMP - INTERVAL '8 days', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 9. INSERT ATTENDANCE SESSIONS (Bổ sung start_time và end_time bắt buộc)
INSERT INTO attendance_sessions (id, course_id, title, session_date, start_time, end_time, created_at, updated_at)
VALUES
    (1, 1, 'Buổi 8: Thực hành tối ưu hóa truy vấn JPA & Giải đáp thắc mắc', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '2 hours', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 10. INSERT ATTENDANCE RECORDS (Sử dụng check_in_time thay vì recorded_at)
INSERT INTO attendance_records (id, session_id, user_id, status, note, check_in_time, created_at, updated_at)
VALUES
    (1, 1, 3, 'PRESENT', 'Học viên tham gia đúng giờ, tích cực phát biểu.', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 4, 'LATE', 'Vào muộn 15 phút do sự cố mạng.', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '15 minutes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 5, 'ABSENT', 'Vắng mặt có phép (Đã gửi email xin nghỉ ốm).', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==============================================================================
-- RESET POSTGRESQL SEQUENCES
-- ==============================================================================
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('courses_id_seq', (SELECT MAX(id) FROM courses));
SELECT setval('modules_id_seq', (SELECT MAX(id) FROM modules));
SELECT setval('lessons_id_seq', (SELECT MAX(id) FROM lessons));
SELECT setval('quizzes_id_seq', (SELECT MAX(id) FROM quizzes));
SELECT setval('questions_id_seq', (SELECT MAX(id) FROM questions));
SELECT setval('question_options_id_seq', (SELECT MAX(id) FROM question_options));
SELECT setval('enrollments_id_seq', (SELECT MAX(id) FROM enrollments));
SELECT setval('attendance_sessions_id_seq', (SELECT MAX(id) FROM attendance_sessions));
SELECT setval('attendance_records_id_seq', (SELECT MAX(id) FROM attendance_records));