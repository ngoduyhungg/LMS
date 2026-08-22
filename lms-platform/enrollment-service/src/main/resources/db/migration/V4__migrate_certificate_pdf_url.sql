-- Chuyển đổi toàn bộ absolute URL thành file name (Object Key)
-- Ví dụ: http://minio:9000/lms-certificates/cert_1.pdf -> cert_1.pdf
UPDATE user_certificates
SET pdf_url = SUBSTRING(pdf_url FROM '[^/]+$')
WHERE pdf_url LIKE 'http%';