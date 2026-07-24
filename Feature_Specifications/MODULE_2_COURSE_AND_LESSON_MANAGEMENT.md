# MODULE 2: COURSE & LESSON MANAGEMENT

## 1. Mục tiêu Module & Kết quả mong đợi
* **Mục tiêu nghiệp vụ:** Cung cấp nền tảng quản trị nội dung học tập phân cấp (Hierarchy Content Management). Giảng viên (Instructor) có thể tạo, tổ chức cấu trúc khóa học theo 3 tầng: **Course (Khóa học) -> Section/Module (Chương/Phần) -> Lesson (Bài học - Video/Text/File)**.
* **Kết quả mong đợi:**
  * Hỗ trợ luồng phê duyệt khóa học (Draft -> Pending Review -> Published/Rejected).
  * Tối ưu hóa hiệu năng đọc/ghi với cấu trúc dữ liệu quan hệ phức tạp, không bị lỗi N+1 Query.
  * Hỗ trợ thao tác sắp xếp lại thứ tự (Reorder/Drag-and-Drop) Chương và Bài học trực quan trên giao diện một cách hiệu quả.

## 2. Workflow (Luồng hoạt động)

### 2.1. Luồng Tạo & Phê duyệt Khóa học (Happy Path)
1. **Instructor Tạo Draft:** Instructor gọi POST `/api/v1/instructor/courses` để tạo thông tin chung (Title, Description, Price, Thumbnail). Khóa học ở trạng thái `DRAFT`.
2. **Xây dựng nội dung (Curriculum):** Instructor tiếp tục thêm các `Section` và trong mỗi Section thêm các `Lesson` (Tải video lên S3/Cloudflare R2 lấy URL hoặc viết nội dung Markdown/HTML).
3. **Gửi phê duyệt (Submit for Review):** Khi hoàn tất, Instructor chuyển trạng thái khóa học sang `PENDING_REVIEW`.
4. **Admin Kiểm duyệt:** Admin xem trước khóa học (Preview Mode).
   * Nếu đạt yêu cầu -> Chuyển trạng thái sang `PUBLISHED` -> Khóa học hiển thị trên Catalog cho Student.
   * Nếu từ chối -> Chuyển trạng thái sang `REJECTED` kèm lý do (`reject_reason`) để Instructor sửa lại.

### 2.2. Luồng Sắp xếp lại thứ tự (Reorder Sections/Lessons) - High Performance Flow
* *Vấn đề:* Giảng viên kéo thả bài học số 10 lên vị trí số 1. Nếu update tuần tự từng record trong DB sẽ gây ra hàng loạt câu lệnh `UPDATE` chậm chạp.
* *Workflow tối ưu:*
  1. **Frontend (React 18):** Sử dụng thư viện `@dnd-kit/core`. Khi người dùng thả chuột (onDragEnd), Frontend tính toán mảng `[{id: lessonId1, orderIndex: 1}, {id: lessonId2, orderIndex: 2}, ...]` và thực hiện **Optimistic UI Update** (cập nhật UI ngay lập tức không cần chờ Backend).
  2. **Backend Call:** Gửi PUT request với Payload là mảng ID và thứ tự mới lên Backend.
  3. **Backend Processing (@Transactional):** Spring Boot nhận danh sách DTO, sử dụng JDBC Batch Update hoặc `saveAll()` đã được tối ưu Batching trong Hibernate để cập nhật toàn bộ `order_index` chỉ trong 1-2 câu lệnh SQL tới PostgreSQL.

## 3. Điểm kỹ thuật cốt lõi (Tech Spotlight)

### 3.1. Giải quyết triệt để lỗi N+1 Query bằng DTO Projection & EntityGraph
* Cấu trúc dữ liệu LMS luôn có tính chất cha-con sâu: `Course` (1) -> `Section` (N) -> `Lesson` (N).
* Nếu dùng `@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)` và viết code chuyển đổi sang DTO bằng vòng lặp trong Service, Hibernate sẽ bắn ra hàng trăm câu query (1 query lấy Course + N query lấy Section + N*M query lấy Lesson).
* **Giải pháp trong Spring Boot 4:** Sử dụng **JPA DTO Projection** kết hợp **`JOIN FETCH`** hoặc **`@EntityGraph`** cho trang chi tiết Khóa học:
```java
@EntityGraph(attributePaths = {"sections", "sections.lessons"})
@Query("SELECT c FROM Course c WHERE c.id = :id AND c.status = 'PUBLISHED'")
Optional<Course> findPublishedCourseWithCurriculum(@Param("id") Long id);
```
* *Lưu ý kiến trúc:* Với trang danh sách Khóa học (Catalog/Pagination), tuyệt đối **KHÔNG** `JOIN FETCH` các collection con vì sẽ làm sai lệch phân trang (Memory Pagination Hazard trong Hibernate). Chỉ fetch bảng `Course` cơ bản.

### 3.2. Bảo mật Video Lesson với Signed URL (AWS S3 / Cloudflare R2)
* Để tránh học viên chia sẻ link video khóa học trả phí ra ngoài public, DB không bao giờ lưu URL public của video.
* **Cơ chế:** DB chỉ lưu `video_key` (path trên S3). Khi Student/Instructor hợp lệ request xem bài học, Backend sử dụng AWS SDK (hoặc S3 Compatible API) để generate ra một **Presigned URL** có thời hạn ngắn (VD: 120 phút) rồi trả về cho React Video Player.

## 4. Lỗi thực tế & Cách debug (Troubleshooting)

### Lỗi 1: MultipleBagFetchException trong Hibernate
* **Hiện tượng:** Khi cố gắng `@EntityGraph(attributePaths = {"sections", "tags"})` (fetch đồng thời 2 Collection kiểu `List<>`), Spring Boot crash lúc khởi động hoặc lúc chạy query với lỗi: `org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags`.
* **Nguyên nhân:** Hibernate không thể ánh xạ tích Đề-các (Cartesian product) của 2 bảng con kiểu `List` không có thứ tự (Bag) trong cùng 1 câu SQL JOIN.
* **Cách Fix:**
  * *Cách 1 (Chuẩn nhất):* Chuyển kiểu dữ liệu từ `List<Section>` và `List<Tag>` sang `Set<Section>` và `Set<Tag>` trong Entity.
  * *Cách 2:* Sử dụng `@OrderColumn` hoặc tách ra thành 2 câu query riêng biệt kết hợp `@BatchSize(size = 20)`.

### Lỗi 2: Circular Reference / StackOverflowError khi Serialize JSON
* **Hiện tượng:** Gọi API GET `/api/v1/courses/{id}`, server treo một lúc rồi ném `java.lang.StackOverflowError` hoặc trả về chuỗi JSON dài vô tận lặp đi lặp lại.
* **Nguyên nhân:** Entity `Course` có `List<Section>`, trong Entity `Section` lại có field `Course course`. Jackson ObjectMapper duyệt qua lại giữa 2 entity vô tận.
* **Cách Fix:**
  * **Nguyên tắc vàng:** KHÔNG BAO GIỜ trả trực tiếp JPA Entity ra Controller. Luôn map sang DTO (sử dụng **MapStruct**).
  * Nếu bắt buộc dùng Entity (không khuyến khích), phải thêm annotation `@JsonIgnore` hoặc `@JsonManagedReference` / `@JsonBackReference`.

### Lỗi 3: React Drag-and-Drop bị giật lag, mất dữ liệu khi lỗi mạng
* **Hiện tượng:** Khi kéo thả sắp xếp bài học, UI bị giật nhảy về chỗ cũ, hoặc khi mạng lỗi thì UI hiển thị thứ tự mới nhưng khi reload trang lại quay về thứ tự cũ.
* **Cách Fix (TanStack Query Optimistic Updates):**
  * Sử dụng `onMutate` của `useMutation` để snapshot lại cache hiện tại và cập nhật cache lập tức theo thứ tự mới.
  * Sử dụng `onError` để rollback lại snapshot cũ nếu API PUT reorder từ Backend thất bại.
  * Sử dụng `onSettled` để `invalidateQueries` đảm bảo data luôn đồng bộ 100% với server.

## 5. Danh sách File quan trọng & API Endpoints

### 5.1. Danh sách File cốt lõi (Backend & Frontend)
```text
[Backend: Spring Boot 4]
├── domain/entity/Course.java              # Entity Khóa học (title, status, price, instructor_id)
├── domain/entity/Section.java             # Entity Chương (title, order_index, course_id)
├── domain/entity/Lesson.java              # Entity Bài học (title, type: VIDEO/TEXT, video_key, order_index)
├── repository/CourseRepository.java       # JPA Repo với custom @Query và @EntityGraph
├── service/CourseService.java             # Nghiệp vụ CRUD, Workflow phê duyệt (@Transactional)
├── service/LessonService.java             # Nghiệp vụ Lesson, logic Reorder Batching
├── service/StorageService.java            # Interface & Impl cho AWS S3 / R2 (Presigned URL)
└── controller/InstructorCourseController.java # API dành riêng cho Instructor

[Frontend: React 18 TypeScript]
├── src/types/course.types.ts              # TypeScript interfaces (CourseDetail, LessonDto, CourseStatus)
├── src/components/course/CurriculumBuilder.tsx # Component quản lý Sections/Lessons
├── src/components/course/LessonDndList.tsx     # Drag-and-Drop list sử dụng @dnd-kit/core
└── src/hooks/queries/useCourseMutations.ts     # TanStack Query custom hooks với Optimistic UI logic
```

### 5.2. Danh sách REST API Endpoints

| Method | Endpoint | Quyền (RBAC) | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/courses` | Public | Lấy danh sách khóa học `PUBLISHED` (có phân trang, lọc, tìm kiếm). |
| **GET** | `/api/v1/courses/{id}` | Public/Student | Lấy chi tiết khóa học + Curriculum (chỉ hiện bài preview nếu chưa mua). |
| **POST** | `/api/v1/instructor/courses` | `ROLE_INSTRUCTOR` | Tạo khóa học mới (Trạng thái mặc định `DRAFT`). |
| **POST** | `/api/v1/instructor/sections` | `ROLE_INSTRUCTOR` | Thêm mới một Chương (Section) vào Khóa học. |
| **PUT** | `/api/v1/instructor/sections/reorder` | `ROLE_INSTRUCTOR` | Cập nhật thứ tự hàng loạt cho các Section (`[{id, orderIndex}]`). |
| **POST** | `/api/v1/instructor/lessons` | `ROLE_INSTRUCTOR` | Thêm Bài học (Lesson) vào Section. |
| **GET** | `/api/v1/lessons/{id}/play-url` | Authenticated | Lấy Presigned URL có thời hạn để phát Video (đã check quyền sở hữu). |
| **PATCH**| `/api/v1/admin/courses/{id}/status` | `ROLE_ADMIN` | Duyệt (`PUBLISHED`) hoặc từ chối (`REJECTED`) khóa học. |

## 6. Checklist triển khai & Đầu ra
- [ ] **Database Migration:** Tạo script Flyway `V2__init_course_curriculum.sql`. Thêm ràng buộc `ON DELETE CASCADE` từ `Course -> Section -> Lesson`. Thêm Index cho `(course_id, order_index)` và `status`.
- [ ] **DTO Mapping:** Cấu hình MapStruct `CourseMapper`, đảm bảo tuyệt đối không có sự rò rỉ JPA Entity ra Controller hoặc vòng lặp vô tận.
- [ ] **SQL Performance Audit:** Bật `spring.jpa.show-sql=true` và kiểm tra Console. Chắc chắn rằng API `GET /api/v1/courses/{id}` chỉ sinh ra **tối đa 1 đến 2 câu lệnh SQL JOIN**, không bị lỗi N+1.
- [ ] **Storage Integration:** Viết Mock Service cho S3 trên môi trường Dev/Local, chuyển sang AWS S3 / Cloudflare R2 thực tế trên Production thông qua Spring Profiles (`@Profile("prod")`).
- [ ] **UI/UX Testing:** Kiểm thử tính năng Kéo thả bài học trên React với mạng chậm (Fast 3G), xác minh tính năng Optimistic Rollback hoạt động đúng khi giả lập Backend lỗi 500.