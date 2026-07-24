# CODING CONVENTIONS & GUIDELINES - LMS PROJECT

Tài liệu này quy định chuẩn đặt tên, thiết kế API và quy trình quản lý mã nguồn cho toàn bộ dự án LMS.

---

## 1. Quy tắc đặt tên (Naming Conventions)

### 1.1. Backend (Spring Boot 4 / Java 21)

| Loại | Quy tắc | Ví dụ chuẩn | Tránh (Không dùng) |
| :--- | :--- | :--- | :--- |
| **Package** | lowercase, viết liền, phân theo feature | com.lms.course, com.lms.auth | com.lms.courseModule, com.lms.Course |
| **Class / Interface** | PascalCase | CourseService, UserRepository | courseService, user_repository |
| **Method / Variable** | camelCase | findByEmail(), totalStudents | Find_By_Email(), Total_Students |
| **Constant** | UPPER_SNAKE_CASE | MAX_RETRY_ATTEMPTS, DEFAULT_ROLE | maxRetry, defaultRole |
| **Entity** | PascalCase, ánh xạ bảng số nhiều | Course, UserProfile (table courses) | tbl_course, CourseDto |
| **DTO (Request/Response)**| Hậu tố Request, Response, hoặc Dto | CreateCourseRequest, CourseResponse | CourseData, CourseInfo |
| **Service** | Interface tên gốc, Impl thêm hậu tố Impl | CourseService, CourseServiceImpl | ICourseService |

### 1.2. Frontend (React 18 / TypeScript)

| Loại | Quy tắc | Ví dụ chuẩn | Tránh (Không dùng) |
| :--- | :--- | :--- | :--- |
| **Component File** | PascalCase.tsx | CourseCard.tsx, Navbar.tsx | courseCard.tsx, navbar.jsx |
| **Utility / Helper** | camelCase.ts | formatDate.ts, storage.ts | FormatDate.ts, utils.ts |
| **Hook File & Name** | Bắt đầu bằng use, camelCase.ts | useAuth.ts, useDebounce.ts | AuthHook.ts, getAuth.ts |
| **Props Interface** | [ComponentName]Props (PascalCase) | CourseCardProps, ButtonProps | ICourseCard, Props |
| **Type / Interface** | PascalCase (Không dùng tiền tố I) | User, CourseModule, PaginatedList | IUser, TModule |
| **CSS / ClassName** | kebab-case (hoặc Tailwind classes) | course-card-wrapper, btn-primary | courseCard, btn_primary |

---

## 2. Quy tắc thiết kế REST API

### 2.1. HTTP Methods & URL Naming
- Danh từ số nhiều cho tài nguyên (Resources), sử dụng kebab-case.
- Không dùng động từ trong URL (trừ các action đặc thù không thể ánh xạ thành CRUD).

| HTTP Method | URL Mẫu | Ý nghĩa |
| :--- | :--- | :--- |
| GET | /api/v1/courses | Lấy danh sách khóa học (hỗ trợ phân trang, lọc) |
| GET | /api/v1/courses/{id} | Lấy chi tiết một khóa học theo ID |
| POST | /api/v1/courses | Tạo mới một khóa học |
| PUT | /api/v1/courses/{id} | Cập nhật toàn bộ thông tin khóa học |
| PATCH | /api/v1/courses/{id}/status | Cập nhật một phần (VD: đổi trạng thái publication) |
| DELETE | /api/v1/courses/{id} | Xóa (Soft-delete) khóa học |
| POST | /api/v1/auth/refresh-token | Action đặc thù (Ngoại lệ cho phép động từ) |

### 2.2. Chuẩn hóa Response Wrapper (ApiResponse<T>)
Mọi endpoint trả về JSON đều phải bọc trong object ApiResponse chuẩn:

    {
      "status": 200,
      "success": true,
      "message": "Lấy danh sách khóa học thành công",
      "data": {
        "content": [...],
        "page": 1,
        "size": 10,
        "totalElements": 100
      },
      "timestamp": "2026-07-17T02:03:33.000Z"
    }

Code Java Wrapper Pattern (sử dụng Java Record):

    public record ApiResponse<T>(
        int status,
        boolean success,
        String message,
        T data,
        Instant timestamp
    ) {
        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(200, true, message, data, Instant.now());
        }

        public static <T> ApiResponse<T> error(int status, String message) {
            return new ApiResponse<>(status, false, message, null, Instant.now());
        }
    }

### 2.3. Xử lý lỗi tập trung (@RestControllerAdvice)
Không dùng try-catch tràn lan trong Controller/Service. Ném ra Custom Exception và để @RestControllerAdvice xử lý:

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex) {
            return ApiResponse.error(404, ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors()
              .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ApiResponse<>(400, false, "Dữ liệu đầu vào không hợp lệ", errors, Instant.now());
        }
    }

---

## 3. Quy tắc Git & Commit (Git Flow & Conventional Commits)

### 3.1. Chuẩn Conventional Commits
Cú pháp commit: type(scope): subject

| Type | Ý nghĩa | Ví dụ |
| :--- | :--- | :--- |
| feat | Thêm tính năng mới | feat(auth): implement JWT login endpoint |
| fix | Sửa lỗi (bug fix) | fix(course): resolve null pointer in course list |
| docs | Cập nhật tài liệu | docs(readme): update setup instructions |
| refactor | Tối ưu code (không đổi logic/tính năng) | refactor(user): clean up user validation logic |
| chore | Cập nhật cấu hình, build tool, dependencies | chore(pom): upgrade spring boot to 3.4.0 |
| test | Thêm hoặc sửa test case | test(service): add unit test for QuizService |

### 3.2. Branching Strategy (Git Flow)
- main (hoặc master): Code Production, chỉ nhận merge từ release hoặc hotfix.
- develop: Branch chính để tích hợp code của team, phản ánh trạng thái chuẩn bị cho bản release tiếp theo.
- feature/<tên-tính-năng>: Tách từ develop để làm tính năng mới. Xong merge lại develop thông qua Pull Request (PR).
- bugfix/<tên-bug>: Tách từ develop để fix bug trong quá trình dev/test.
- hotfix/<tên-bug-gấp>: Tách trực tiếp từ main để sửa lỗi gấp trên Production. Xong merge ngược lại vào cả main và develop.