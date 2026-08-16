-- Seed dữ liệu cục bộ giả lập từ Kafka cho course_metrics
INSERT INTO course_metrics (course_id, total_lessons, updated_at)
VALUES (1, 24, CURRENT_TIMESTAMP);

INSERT INTO course_metrics (course_id, total_lessons, updated_at)
VALUES (2, 15, CURRENT_TIMESTAMP);

-- Seed các mẫu chứng chỉ mặc định của giảng viên cho 2 khóa học trên
INSERT INTO certificates (course_id, title, template_url, created_at)
VALUES (1, 'Chứng chỉ Hoàn thành Khóa học Java Spring Boot', 'https://s3.amazonaws.com/lms/templates/java_spring_cert.pdf', CURRENT_TIMESTAMP);

INSERT INTO certificates (course_id, title, template_url, created_at)
VALUES (2, 'Chứng chỉ Lập trình C++ Cơ bản', 'https://s3.amazonaws.com/lms/templates/cpp_basic_cert.pdf', CURRENT_TIMESTAMP);