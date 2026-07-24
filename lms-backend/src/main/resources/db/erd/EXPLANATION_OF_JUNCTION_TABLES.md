# Architecture Explanation & Junction Tables

Thiết kế LMS thường gặp lỗi sai lầm là lạm dụng kiểu dữ liệu JSONB hoặc Array của PostgreSQL để lưu danh sách ID (ví dụ: lưu mảng `role_ids` vào bảng `users`, hoặc `completed_lesson_ids` vào bảng `enrollments`)[cite: 3]. Điều này phá vỡ chuẩn hóa 3NF, gây chậm truy vấn, không tạo được Foreign Key Constraint và cực kỳ khó viết báo cáo thống kê (Analytics)[cite: 3].

Dưới đây là lý do kiến trúc cần các bảng trung gian chiến lược sau[cite: 3]:

## user_roles và role_permissions (Auth / RBAC)
* **Vấn đề tránh được:** Tránh hardcode role/permission trong mã nguồn hoặc cấu trúc bảng mảng[cite: 3].
* **Lý do tối ưu:** Khi một hệ thống LMS lớn lên, phân quyền không chỉ dừng lại ở `ADMIN`, `INSTRUCTOR`, `STUDENT` mà còn có `TEACHING_ASSISTANT`, `COURSE_REVIEWER`, `FINANCE_MANAGER`[cite: 3]. Việc tách thành 2 bảng trung gian giúp Spring Security (kết hợp JPA/Hibernate) load danh sách quyền của user trong đúng 1 câu lệnh JOIN có sử dụng Index B-Tree, cho phép phân quyền động (Dynamic RBAC) mà không cần deploy lại ứng dụng[cite: 3].

## enrollments và course_progress (Enrollment / Progress)
* **Vấn đề tránh được:** Tránh "Nhồi nhét" thông tin tiến độ (chạy liên tục theo thời gian thực) vào bảng định danh khóa học[cite: 3].
* **Lý do tối ưu:**
  * `enrollments`: Đóng vai trò là hợp đồng giữa học viên và khóa học (lưu trạng thái thanh toán, ngày đăng ký, ngày hết hạn, trạng thái khóa học như `ACTIVE`, `REFUNDED`, `CANCELLED`)[cite: 3].
  * `course_progress`: Là quan hệ 1-1 với `enrollments` (`enrollment_id` là UNIQUE FK)[cite: 3]. Bảng này chịu tải ghi cực lớn (High Write Throughput) vì mỗi lần học viên xem xong 1 video, hệ thống phải cập nhật `progress_percentage` và `last_accessed_lesson_id`[cite: 3]. Việc tách riêng giúp giảm Lock Contention (xung đột khóa) trên bảng `enrollments`, giúp API Dashboard load cực nhanh mà không bị nghẽn bởi các giao dịch thanh toán hoặc ghi danh chạy ngầm[cite: 3].

## quiz_submissions và submission_answers (Quiz / Assessment)
* **Vấn đề tránh được:** Tránh mất tính toàn vẹn lịch sử thi (Historical Integrity) khi giảng viên chỉnh sửa đề thi sau này[cite: 3].
* **Lý do tối ưu:**
  * Học viên có thể làm một bài thi nhiều lần (`max_attempts`)[cite: 3]. Bảng `quiz_submissions` đóng vai trò là "phiên làm bài" (Lưu `attempt_number`, `started_at`, `submitted_at`, điểm tổng kết)[cite: 3].
  * Bảng `submission_answers` lưu câu trả lời chi tiết cho từng câu hỏi trong lần làm bài đó[cite: 3]. Nếu giảng viên thay đổi đáp án đúng của câu hỏi gốc sau đó 1 tháng, điểm số và lịch sử bài làm cũ của học viên trong bảng `submission_answers` và `quiz_submissions` hoàn toàn không bị ảnh hưởng, phục vụ chính xác cho việc kiểm định chất lượng đào tạo (Audit & Accreditation)[cite: 3].