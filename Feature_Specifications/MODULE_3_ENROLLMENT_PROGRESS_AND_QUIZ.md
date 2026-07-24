# MODULE 3: ENROLLMENT, PROGRESS TRACKING & QUIZ ENGINE

## 1. Mục tiêu Module & Kết quả mong đợi
* **Mục tiêu nghiệp vụ:** Quản lý vòng đời học tập của Học viên (Student Lifecycle). Bao gồm việc đăng ký khóa học (Enrollment), ghi nhận tiến độ học tập thời gian thực đến từng bài học (Progress Tracking), và hệ thống kiểm tra đánh giá trắc nghiệm có tính giờ, chấm điểm tự động (Timed Quiz Engine).
* **Kết quả mong đợi:**
  * Cơ chế ghi nhận tiến độ học tập chính xác, không bị gian lận (spam API) và xử lý tốt vấn đề đồng thời (Concurrency).
  * Hệ thống làm bài thi trắc nghiệm an toàn, không rò rỉ đáp án xuống máy khách (Client-side exposure), tự động nộp bài khi hết giờ ở phía Server.
  * Tự động phát đi sự kiện hoàn thành khóa học (Course Completed Event) khi tiến độ đạt 100%.

## 2. Workflow (Luồng hoạt động)

### 2.1. Luồng Ghi nhận Tiến độ học tập (Progress Tracking Workflow)
1. **Video Watch/Read Action:** Học viên xem video (đạt thời lượng tối thiểu, VD: > 80% thời lượng) hoặc đọc xong bài Text -> React Player/UI phát sự kiện `onLessonCompleted`.
2. **API Call:** Frontend gửi POST `/api/v1/progress/lessons/{lessonId}/complete`.
3. **Backend Transactional Processing:**
   * Kiểm tra học viên đã sở hữu khóa học chưa qua bảng `enrollments`.
   * Tìm hoặc tạo mới record trong bảng `lesson_progress` với `status = COMPLETED`, `completed_at = NOW()`.
   * **Tính toán % khóa học:** Chạy câu lệnh SQL Aggregate đếm tổng số bài học đã hoàn thành của user chia cho tổng số bài học của khóa học. Cập nhật con số `progress_percentage` vào bảng `enrollments`.
4. **Event Trigger (Kiến trúc hướng sự kiện):** Nếu `progress_percentage == 100.00%` -> Spring Boot bắn ra một sự kiện bất đồng bộ `CourseCompletedEvent(userId, courseId)`. (Sự kiện này sẽ được Module 4 lắng nghe để tạo chứng chỉ).

### 2.2. Luồng Làm bài Quiz & Chấm điểm tự động (Quiz Engine Flow)
1. **Bắt đầu làm bài (Start Attempt):** Student gọi POST `/api/v1/quizzes/{quizId}/start`. Backend tạo một record trong bảng `quiz_attempts` với `start_time = NOW()`, `status = IN_PROGRESS`, và tính `end_time = start_time + duration_minutes`.
2. **Trả đề thi (Safe Payload):** Backend trả về danh sách câu hỏi và các lựa chọn (Options). **Tuyệt đối loại bỏ** trường `is_correct` và `explanation` ra khỏi DTO trả về cho Frontend.
3. **Nộp bài (Submit Quiz):** Student chọn đáp án và gọi POST `/api/v1/quizzes/attempts/{attemptId}/submit` với payload là danh sách `[{questionId, selectedOptionId}]`.
4. **Auto-Grading & Security Check (Backend):**
   * *Edge Case (Nộp muộn):* Kiểm tra `NOW() > attempt.end_time + 30 seconds` (độ trễ mạng cho phép). Nếu quá hạn -> Từ chối nộp hoặc chỉ tính điểm các câu đã lưu nháp trước đó.
   * *Chấm điểm:* Backend đối chiếu đáp án với DB, tính điểm (`score`), cập nhật `status = COMPLETED`, `passed = (score >= passing_score)`.
   * *Kết quả:* Trả về kết quả chi tiết kèm đáp án đúng và lời giải thích (`explanation`) cho Student ôn tập.

## 3. Điểm kỹ thuật cốt lõi (Tech Spotlight)

### 3.1. Xử lý Concurrency & Tránh Duplicate Progress bằng DB Constraints
* **Vấn đề:** Người dùng mở 2 tab trình duyệt cùng xem 1 bài học, hoặc click nháy đúp liên tục vào nút "Đánh dấu hoàn thành". Nếu Backend chỉ dùng logic `if (!exists) { save(); }`, trong môi trường đa luồng (Multi-threading) sẽ xảy ra Race Condition dẫn đến việc insert 2 dòng `lesson_progress` giống nhau, làm sai lệch phân số tính % hoàn thành.
* **Giải pháp Kỹ thuật:**
  1. Tạo ràng buộc duy nhất ở mức Cơ sở dữ liệu (Database Unique Constraint): `UNIQUE(user_id, lesson_id)` trên bảng `lesson_progress`.
  2. Ở tầng Spring Boot Repository, sử dụng câu lệnh **Native SQL UPSERT (ON CONFLICT)** của PostgreSQL để đạt hiệu năng tối đa và không bao giờ bị lỗi Transactional:
```sql
INSERT INTO lesson_progress (user_id, lesson_id, status, completed_at)
VALUES (:userId, :lessonId, 'COMPLETED', NOW())
ON CONFLICT (user_id, lesson_id) 
DO UPDATE SET status = 'COMPLETED', completed_at = NOW();
```

### 3.2. Spring Event-Driven Architecture (Tách biệt Module)
* Để không làm chậm request "Hoàn thành bài học cuối cùng" của User bằng việc phải chờ generate PDF chứng chỉ (rất nặng), chúng ta áp dụng **Spring Event**:
```java
// Trong ProgressService.java
if (newProgress == 100.0) {
    applicationEventPublisher.publishEvent(new CourseCompletedEvent(this, userId, courseId));
}
```
* Sự kiện này được xử lý bất đồng bộ nhờ `@Async` và `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` tại Module 4, đảm bảo transaction ghi nhận tiến độ học tập đã thành công vào DB rồi mới bắt đầu tạo chứng chỉ.

## 4. Lỗi thực tế & Cách debug (Troubleshooting)

### Lỗi 1: Gian lận Quiz do lộ đáp án qua Network Tab
* **Hiện tượng:** Học viên mở DevTools -> Network Tab, xem Response JSON của API lấy đề thi và thấy rõ `isCorrect: true` ở các option, dẫn đến việc đạt 100% điểm mà không cần học.
* **Nguyên nhân:** Dùng chung 1 class DTO `QuestionDto` cho cả lúc "Lấy đề thi" và lúc "Xem kết quả thi", hoặc dùng trực tiếp Entity mapping ra JSON.
* **Cách Fix:** Tách biệt triệt để DTO:
  * `QuizTakeQuestionDto`: Chỉ có `id`, `content`, `options: [{id, content}]`. (Không có field `isCorrect`, `explanation`).
  * `QuizResultQuestionDto`: Có đầy đủ thông tin để hiển thị sau khi đã nộp bài thành công.

### Lỗi 2: Lỗi Deadlock hoặc Tính sai % Tiến độ khi nhiều bài học hoàn thành cùng lúc
* **Hiện tượng:** Khi sử dụng công cụ automation test bắn 5 request hoàn thành 5 bài học khác nhau của cùng 1 khóa học trong 50ms, % tiến độ bị tính sai (VD: làm xong 5/10 bài nhưng DB chỉ ghi nhận 30% do tình trạng Race Condition khi đọc/ghi bảng `enrollments`).
* **Nguyên nhân:** Các luồng (Threads) đọc giá trị `completed_lessons` cũ lên bộ nhớ Java, cộng 1, rồi ghi đè lên nhau (Lost Update Problem).
* **Cách Fix:** Tuyệt đối không tính toán % bằng code Java (Read-then-Write). Hãy để PostgreSQL tự tính bằng một câu query duy nhất trong `@Transactional`:
```sql
UPDATE enrollments e
SET progress_percentage = (
    SELECT CAST(COUNT(lp.id) AS DECIMAL(5,2)) / (SELECT COUNT(l.id) FROM lessons l JOIN sections s ON l.section_id = s.id WHERE s.course_id = e.course_id) * 100.0
    FROM lesson_progress lp
    JOIN lessons l2 ON lp.lesson_id = l2.id
    JOIN sections s2 ON l2.section_id = s2.id
    WHERE lp.user_id = e.user_id AND s2.course_id = e.course_id AND lp.status = 'COMPLETED'
)
WHERE e.user_id = :userId AND e.course_id = :courseId;
```

### Lỗi 3: React Video Player Spam API Complete
* **Hiện tượng:** Học viên kéo thanh thời gian (seeking/scrubbing) của Video tới lui liên tục, mỗi lần thả chuột Video Player lại bắn API complete làm server bị DDoS nhẹ bởi chính user.
* **Cách Fix (React 18 Debounced/Ref Guard):**
  * Sử dụng `useRef` để lưu trạng thái `isMarkingComplete = useRef(false)`.
  * Chỉ trigger gọi API khi event `onEnded` của HTML5 Video phát ra, kết hợp với kiểm tra `currentTime >= duration * 0.9`.
  * Dùng `lodash.debounce` hoặc custom hook `useThrottle` để giới hạn tần suất gọi API tối đa 1 lần/5 giây.

## 5. Danh sách File quan trọng & API Endpoints

### 5.1. Danh sách File cốt lõi (Backend & Frontend)
```text
[Backend: Spring Boot 4]
├── domain/entity/Enrollment.java          # Entity Đăng ký học (user_id, course_id, progress_percentage, status)
├── domain/entity/LessonProgress.java      # Entity Tiến độ bài học (user_id, lesson_id, status, completed_at)
├── domain/entity/Quiz.java                # Entity Bài kiểm tra (title, duration_minutes, passing_score)
├── domain/entity/Question.java            # Entity Câu hỏi (content, quiz_id, type: SINGLE_CHOICE/MULTI)
├── domain/entity/AnswerOption.java        # Entity Đáp án (content, is_correct, explanation, question_id)
├── domain/entity/QuizAttempt.java         # Entity Lần thi (user_id, quiz_id, start_time, end_time, score)
├── repository/LessonProgressRepository.java # JPA Repo chứa native query ON CONFLICT UPSERT
├── service/ProgressService.java           # Logic xử lý tiến độ, tính % và publish CourseCompletedEvent
├── service/QuizService.java               # Logic chấm thi tự động (Auto-grading) & Time validation
└── event/CourseCompletedEvent.java        # Spring ApplicationEvent object

[Frontend: React 18 TypeScript]
├── src/types/quiz.types.ts                # TypeScript interfaces (QuizTakeDto, QuizSubmitPayload, QuizResult)
├── src/components/player/VideoPlayer.tsx  # Video wrapper xử lý logic onEnded & progress tracking
├── src/components/quiz/QuizRunner.tsx     # Component làm bài thi có Đếm ngược thời gian (Timer countdown)
└── src/hooks/useQuizTimer.ts              # Custom hook quản lý thời gian làm bài chính xác từ server_time
```

### 5.2. Danh sách REST API Endpoints

| Method | Endpoint | Quyền (RBAC) | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/enrollments` | `ROLE_STUDENT` | Đăng ký vào một khóa học (Tạo enrollment record). |
| **GET** | `/api/v1/enrollments/my-courses` | `ROLE_STUDENT` | Lấy danh sách khóa học đang học kèm `%` tiến độ. |
| **POST** | `/api/v1/progress/lessons/{id}/complete` | `ROLE_STUDENT` | Đánh dấu hoàn thành bài học, tự động tính lại `%` khóa học. |
| **GET** | `/api/v1/quizzes/{id}/attempt-info` | `ROLE_STUDENT` | Lấy thông tin quiz (thời gian, số câu hỏi, điểm đạt) trước khi thi. |
| **POST** | `/api/v1/quizzes/{id}/start` | `ROLE_STUDENT` | Bắt đầu làm bài, tạo `attempt_id`, nhận danh sách câu hỏi (Secure DTO). |
| **POST** | `/api/v1/quizzes/attempts/{id}/submit` | `ROLE_STUDENT` | Nộp bài thi, Server tự động chấm điểm và trả về kết quả. |
| **GET** | `/api/v1/quizzes/attempts/{id}/result` | `ROLE_STUDENT` | Xem lại chi tiết kết quả lần thi, đáp án đúng và lời giải thích. |

## 6. Checklist triển khai & Đầu ra
- [ ] **Database Migration:** Tạo script Flyway `V3__init_enrollment_progress_quiz.sql`. Đảm bảo có ràng buộc `UNIQUE(user_id, lesson_id)` trên bảng `lesson_progress` và `UNIQUE(user_id, course_id)` trên bảng `enrollments`.
- [ ] **Concurrency & Locking Test:** Viết Integration Test sử dụng `ExecutorService` (Java) với 10 luồng đồng thời gọi API complete lesson. Đảm bảo `%` cuối cùng trong bảng `enrollments` chính xác tuyệt đối, không có lỗi OptimisticLockException hoặc Deadlock.
- [ ] **Security Audit (Quiz):** Kiểm tra kỹ Response Body của API `POST /api/v1/quizzes/{id}/start` bằng Postman. Xác nhận 100% không có field `isCorrect` hoặc `explanation` bị lọt ra ngoài.
- [ ] **Frontend Timer Check:** Đảm bảo UI đếm ngược thời gian làm bài (`useQuizTimer`) phải đồng bộ với thời gian server (`end_time`), tự động trigger submit form khi thời gian về `00:00`, không phụ thuộc hoàn toàn vào đồng hồ máy tính của user (tránh user chỉnh giờ hệ thống để gian lận).