# 📋 BÁO CÁO NGHIỆM THU & PHÂN TÍCH HIỆN TRẠNG — `lms-backend`
> **Thời điểm quét:** 2026-07-23 | **Stack:** Spring Boot 4.1.0 · Java 21 · PostgreSQL · Flyway · Spring Security (OAuth2 JWT)

---

## PHẦN 1: KIỂM TOÁN CẤU TRÚC & CHUẨN MỰC

### 1.1 — Quy hoạch Modular Monolith

Dự án tuân thủ kiến trúc **Modular Monolith** với root package `com.lms`, phân tách rõ ràng thành 2 nhánh chính:

```
com.lms
├── common/          ← Tầng dùng chung (infrastructure-level)
│   ├── config/
│   ├── entity/
│   ├── exception/
│   └── response/
└── modules/         ← Tầng nghiệp vụ (domain-level)
    ├── attendance/
    ├── auth/        ✅ IMPLEMENTED
    ├── certificate/
    ├── course/      🔶 PARTIAL (~30%)
    ├── enrollment/
    ├── notification/
    └── quiz/
```

**Đánh giá độ cô lập:**

| Module | Trạng thái | Ghi chú cô lập |
|--------|-----------|----------------|
| `auth` | ✅ Hoạt động | Độc lập tốt. Chỉ phụ thuộc `common` |
| `course` | 🔶 Một phần | Phụ thuộc `auth.entity.User` qua `Course.instructor` — **hợp lệ** |
| `attendance` | ⬜ Chưa triển khai | Chỉ có schema DB |
| `certificate` | ⬜ Chưa triển khai | Chỉ có schema DB |
| `enrollment` | ⬜ Chưa triển khai | Chỉ có schema DB |
| `notification` | ⬜ Chưa triển khai | Chỉ có schema DB |
| `quiz` | ⬜ Chưa triển khai | Chỉ có schema DB |

> [!NOTE]
> Module `course` tham chiếu `User` từ `auth` — đây là phụ thuộc một chiều **hợp lệ** trong Modular Monolith. Không vi phạm nguyên tắc cô lập.

---

### 1.2 — Tầng Common: Kiểm tra sự hiện diện & chuẩn mực

| Class | File | Trạng thái | Nhận xét |
|-------|------|-----------|---------|
| `ApiResponse<T>` | `common/response/ApiResponse.java` | ✅ Chuẩn | Java **record**, immutable. Có đủ 4 factory methods: `success()`, `success(msg, data)`, `successMessage()`, `error()` |
| `AppJwtProperties` | `common/config/AppJwtProperties.java` | ✅ Chuẩn | `@ConfigurationProperties` + `@Validated` record — chuẩn Spring Boot 4 |
| `JwtConfig` | `common/config/JwtConfig.java` | ⚠️ Cần chú ý | Secret key bị **hardcode** thay vì đọc từ `AppJwtProperties.secret` |
| `SecurityConfig` | `common/config/SecurityConfig.java` | ✅ Chuẩn | Stateless, CSRF disabled, OAuth2 Resource Server JWT, custom entry points |
| `BaseEntity` | `common/entity/BaseEntity.java` | ✅ Chuẩn | `@MappedSuperclass`, `@Id`, `IDENTITY` strategy |
| `AuditableEntity` | `common/entity/AuditableEntity.java` | ✅ Chuẩn | Kế thừa `BaseEntity`, `createdAt`/`updatedAt` với Hibernate annotations |
| `CustomAccessDeniedHandler` | `common/exception/` | ✅ Chuẩn | Trả JSON chuẩn với HTTP 403 |
| `CustomAuthenticationEntryPoint` | `common/exception/` | ✅ Chuẩn | Trả JSON chuẩn với HTTP 401 |

> [!WARNING]
> **Bug tiềm ẩn:** `JwtConfig.java` hardcode `SECRET_KEY` trong class field thay vì inject từ `AppJwtProperties`. `JwtTokenProvider` cũng hardcode `validityInSeconds = 900` thay vì đọc từ `AppJwtProperties.accessTokenTtlMinutes`. Cần refactor để đồng bộ.

---

### 1.3 — Tuân thủ Design Pattern

#### DTO Pattern

| Đánh giá | Chi tiết |
|----------|---------|
| ✅ **Tuân thủ** | `CourseController` trả về `CourseResponse` (DTO), không expose JPA Entity |
| ✅ **Tuân thủ** | `AuthController` trả về `AuthResponse` (DTO) |
| ✅ Không vi phạm | Không tìm thấy bất kỳ chỗ nào return Entity trực tiếp ở Controller |

#### Service Layer Pattern

| Đánh giá | Chi tiết |
|----------|---------|
| ✅ **Tuân thủ** | `AuthService.java` (interface) + `AuthServiceImpl.java` (triển khai) |
| ✅ **Tuân thủ** | `CourseService.java` (interface) + `CourseServiceImpl.java` (triển khai) |
| ✅ Chuẩn | Tất cả Impl nằm trong `service/impl/` |

#### Security Pattern

| Hạng mục | Triển khai | Đánh giá |
|----------|-----------|---------|
| **Mã hóa mật khẩu** | `PasswordEncoderFactories.createDelegatingPasswordEncoder()` | ✅ Chuẩn — bcrypt mặc định, future-proof |
| **JWT Generation** | Spring Security OAuth2 JWT (`NimbusJwtEncoder`, `HS256`) | ✅ Chuẩn enterprise |
| **JWT Validation** | `NimbusJwtDecoder` làm OAuth2 Resource Server | ✅ Chuẩn — không cần custom filter |
| **RBAC** | `CustomUserDetails.getAuthorities()` tích hợp Role + Permission | ✅ Granular RBAC |
| **`@PreAuthorize`** | **CHƯA được sử dụng** ở bất kỳ Controller nào | ⚠️ **Thiếu** |
| **`@EnableMethodSecurity`** | **CHƯA được bật** | ⚠️ **Thiếu** |

---

## PHẦN 2: MA TRẬN TIẾN ĐỘ THỰC TẾ (STATUS INVENTORY MATRIX)

### 2.1 — Database & Flyway

| Migration | File | Nội dung |
|-----------|------|---------|
| V1 | `V1__init_schema.sql` | Tạo **17 bảng** + indexes đầy đủ |
| V2 | `V2__insert_seed_data.sql` | Seed data phong phú cho dev/test |

**17 bảng được tạo bởi V1:**

| Module DB | Bảng | Quan hệ chính |
|-----------|------|--------------|
| AUTH/RBAC | `users`, `roles`, `permissions` | — |
| AUTH/RBAC | `user_roles`, `role_permissions` | FK users↔roles↔permissions |
| AUTH | `refresh_tokens` | FK → users |
| COURSE | `categories` | Self-referencing (parent_id) |
| COURSE | `courses` | FK → users (instructor), categories |
| COURSE | `modules` | FK → courses (CASCADE DELETE) |
| COURSE | `lessons` | FK → modules (CASCADE DELETE) |
| COURSE | `lesson_resources` | FK → lessons (CASCADE DELETE) |
| ENROLLMENT | `enrollments` | FK → users, courses |
| PROGRESS | `course_progress`, `lesson_progress` | FK → enrollments, lessons |
| QUIZ | `quizzes`, `questions`, `question_options` | FK → courses, lessons |
| QUIZ | `quiz_submissions`, `submission_answers` | FK → quizzes, users, enrollments |
| ATTENDANCE | `attendance_sessions`, `attendance_records` | FK → courses, users |
| NOTIFICATION | `notifications`, `user_notifications` | FK → users |
| CERTIFICATE | `certificates`, `user_certificates` | FK → courses, users, enrollments |

**Seed Data (V2):**

| Bảng | Số bản ghi | Nội dung |
|------|-----------|---------|
| `roles` | 3 | ADMIN, INSTRUCTOR, STUDENT |
| `users` | 5 | 1 admin, 1 instructor, 3 student (pass: `123456`) |
| `user_roles` | 5 | Phân quyền đầy đủ |
| `courses` | 2 | Spring Boot 4 + React 18 |
| `modules` | 3 | 2 modules/course 1, 1/course 2 |
| `lessons` | 5 | Mix VIDEO + TEXT, có is_preview |
| `quizzes` | 1 | Gắn với lesson IoC/DI |
| `questions` | 3 | SINGLE_CHOICE |
| `question_options` | 12 | 4 options/question |
| `enrollments` | 3 | 3 students → course 1 |
| `attendance_sessions` | 1 | 1 buổi học |
| `attendance_records` | 3 | PRESENT/LATE/ABSENT |

---

### 2.2 — Module `auth`: Hoàn thiện 100% ✅

| Layer | File | Method/Endpoint |
|-------|------|----------------|
| **Entity** | `User.java` | email, passwordHash, fullName, avatarUrl, phoneNumber, status, roles (ManyToMany LAZY) |
| **Entity** | `Role.java` | name, description, permissions (ManyToMany LAZY) |
| **Entity** | `Permission.java` | name, module, description |
| **Entity** | `RefreshToken.java` | Có entity, chưa dùng trong service |
| **Repository** | `UserRepository` | `findByEmail()`, `existsByEmail()` |
| **Repository** | `RoleRepository` | `findByName()` |
| **DTO** | `LoginRequest` | email, password (Jakarta Validation) |
| **DTO** | `RegisterRequest` | email, password, fullName, phoneNumber (Jakarta Validation) |
| **DTO** | `AuthResponse` | accessToken, email, fullName |
| **Security** | `CustomUserDetails` | Implements UserDetails, gộp Role + Permission thành authorities |
| **Security** | `JwtTokenProvider` | `generateAccessToken()` — HS256 |
| **Service** | `AuthService` (interface) | `register()`, `login()` |
| **Service** | `AuthServiceImpl` | `register()` + `login()` với BCrypt + JWT |
| **Controller** | `AuthController` | `POST /api/auth/register`, `POST /api/auth/login` |

---

### 2.3 — Module `course`: Hoàn thiện ~30% 🔶

#### Tầng Entity

| Entity | File | Ánh xạ JPA | Thiếu sót |
|--------|------|-----------|---------|
| `Course` | ✅ Có | `@ManyToOne(LAZY)` → User, Category | **THIẾU** `@OneToMany` → modules |
| `Module` | ✅ Có | `@ManyToOne(LAZY)` → Course | **THIẾU** `@OneToMany` → lessons |
| `Lesson` | ✅ Có | `@ManyToOne(LAZY)` → Module | **THIẾU** `@OneToMany` → lessonResources |
| `LessonResource` | ✅ Có | `@ManyToOne(LAZY)` → Lesson | Đủ cho Entity con |
| `Category` | ✅ Có | `@ManyToOne(LAZY)` → Category (self-ref) | **THIẾU** `@OneToMany` → children |

> [!IMPORTANT]
> Tất cả 5 Entity đều định nghĩa chiều **Child → Parent** (`@ManyToOne`) nhưng **không có chiều ngược** (`@OneToMany`). Điều này khiến không thể truy xuất curriculum hierarchy (`Course → List<Module> → List<Lesson>`) qua JPA.

#### Tầng Repository

| Repository | Trạng thái | Custom Queries |
|-----------|-----------|--------------|
| `CourseRepository` | ✅ Có | `findBySlug(String)`, `findAllByStatus(CourseStatus)` |
| `ModuleRepository` | ❌ **THIẾU** | — |
| `LessonRepository` | ❌ **THIẾU** | — |
| `LessonResourceRepository` | ❌ **THIẾU** | — |
| `CategoryRepository` | ❌ **THIẾU** | — |

#### Tầng DTO

| DTO | Trạng thái | Nội dung |
|-----|-----------|---------|
| `CourseResponse` | ✅ Có | id, title, slug, description, thumbnailUrl, price, status, instructorId, instructorName, createdAt, updatedAt |
| `CourseUpsertRequest` | ❌ **THIẾU** | DTO tạo/sửa course (cần Jakarta Validation) |
| `ModuleResponse` | ❌ **THIẾU** | DTO hiển thị thông tin module |
| `LessonResponse` | ❌ **THIẾU** | DTO hiển thị thông tin lesson |
| `CourseCurriculumResponse` | ❌ **THIẾU** | DTO cây chương trình (Course → List<Module> → List<Lesson>) |
| `ModuleUpsertRequest` | ❌ **THIẾU** | DTO tạo/sửa module |
| `LessonUpsertRequest` | ❌ **THIẾU** | DTO tạo/sửa lesson |

#### Tầng Service

| Method | Trạng thái |
|--------|-----------|
| `getAllPublishedCourses()` | ✅ Đã viết xong |
| `getCourseDetail(String slug)` | ✅ Đã viết xong |
| `createCourse(CourseUpsertRequest, Long instructorId)` | ❌ **THIẾU** |
| `updateCourse(Long id, CourseUpsertRequest)` | ❌ **THIẾU** |
| `deleteCourse(Long id)` | ❌ **THIẾU** |
| `getCurriculum(Long courseId)` | ❌ **THIẾU** |
| `addModule(Long courseId, ModuleUpsertRequest)` | ❌ **THIẾU** |
| `updateModule(Long moduleId, ModuleUpsertRequest)` | ❌ **THIẾU** |
| `deleteModule(Long moduleId)` | ❌ **THIẾU** |
| `addLesson(Long moduleId, LessonUpsertRequest)` | ❌ **THIẾU** |
| `updateLesson(Long lessonId, LessonUpsertRequest)` | ❌ **THIẾU** |
| `deleteLesson(Long lessonId)` | ❌ **THIẾU** |

#### Tầng Controller

| Endpoint | Auth Required | Trạng thái |
|----------|--------------|-----------|
| `GET /api/courses` | authenticated | ✅ Có |
| `GET /api/courses/{slug}` | authenticated | ✅ Có |
| `POST /api/courses` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `PUT /api/courses/{id}` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `DELETE /api/courses/{id}` | ADMIN | ❌ **THIẾU** |
| `GET /api/courses/{id}/curriculum` | authenticated | ❌ **THIẾU** |
| `POST /api/courses/{courseId}/modules` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `PUT /api/courses/modules/{moduleId}` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `DELETE /api/courses/modules/{moduleId}` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `POST /api/courses/modules/{moduleId}/lessons` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `PUT /api/courses/lessons/{lessonId}` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |
| `DELETE /api/courses/lessons/{lessonId}` | INSTRUCTOR/ADMIN | ❌ **THIẾU** |

---

## PHẦN 3: PHÂN TÍCH KHOẢNG TRỐNG CHO GIAI ĐOẠN 4 (GAP ANALYSIS)

### Gap 1 — Entity: Thiếu quan hệ `@OneToMany`

| Entity cần sửa | Quan hệ cần thêm | Lý do |
|---------------|-----------------|-------|
| `Course.java` | `@OneToMany(mappedBy="course", cascade=ALL, orphanRemoval=true, fetch=LAZY)` + `@JsonIgnore` → `List<Module>` | Build curriculum tree |
| `Module.java` | `@OneToMany(mappedBy="module", cascade=ALL, orphanRemoval=true, fetch=LAZY)` + `@JsonIgnore` → `List<Lesson>` | Build curriculum tree |
| `Lesson.java` | `@OneToMany(mappedBy="lesson", fetch=LAZY)` + `@JsonIgnore` → `List<LessonResource>` | Trả về tài nguyên bài học |

### Gap 2 — Repository: Thiếu 4 Repository

| Repository cần tạo | Custom Methods cần có |
|-------------------|----------------------|
| `ModuleRepository` | `findAllByCourseIdOrderBySortOrder(Long courseId)` |
| `LessonRepository` | `findAllByModuleIdOrderBySortOrder(Long moduleId)` |
| `LessonResourceRepository` | `findAllByLessonId(Long lessonId)` |
| `CategoryRepository` | `findBySlug(String slug)`, `findAllByParentIsNull()` |

### Gap 3 — DTO: Thiếu 6 DTO class

**Luồng Đọc (Response):**
- `LessonResponse` — id, title, lessonType, durationSeconds, isPreview, sortOrder
- `ModuleResponse` — id, title, sortOrder, `List<LessonResponse>`
- `CourseCurriculumResponse` — id, title, slug, `List<ModuleResponse>`

**Luồng Ghi (Request + Jakarta Validation):**
- `CourseUpsertRequest` — title(`@NotBlank @Size(max=255)`), summary, description, price(`@DecimalMin`), level(`@NotNull CourseLevel`), categoryId, thumbnailUrl
- `ModuleUpsertRequest` — title(`@NotBlank`), sortOrder(`@Min(0)`)
- `LessonUpsertRequest` — title(`@NotBlank`), content, videoUrl, durationSeconds, lessonType(`@NotNull`), isPreview, sortOrder

### Gap 4 — Service: Thiếu 10 method nghiệp vụ

```java
// Luồng ghi Course
CourseResponse createCourse(CourseUpsertRequest request, Long instructorId);
CourseResponse updateCourse(Long id, CourseUpsertRequest request);
void deleteCourse(Long id);

// Curriculum Tree (READ)
CourseCurriculumResponse getCurriculum(Long courseId);

// Module CRUD
ModuleResponse addModule(Long courseId, ModuleUpsertRequest request);
ModuleResponse updateModule(Long moduleId, ModuleUpsertRequest request);
void deleteModule(Long moduleId);

// Lesson CRUD
LessonResponse addLesson(Long moduleId, LessonUpsertRequest request);
LessonResponse updateLesson(Long lessonId, LessonUpsertRequest request);
void deleteLesson(Long lessonId);
```

### Gap 5 — Security: Thiếu `@EnableMethodSecurity` + `@PreAuthorize`

| Hạng mục | Hiện trạng | Cần làm |
|---------|-----------|--------|
| `@EnableMethodSecurity` | ❌ Chưa bật | Thêm vào `SecurityConfig` |
| `@PreAuthorize` trên write endpoints | ❌ Không có | Gắn vào POST/PUT/DELETE |
| Public GET endpoints | Yêu cầu auth | Cập nhật `SecurityConfig` để `permitAll()` cho course catalog |

### Gap 6 — Exception Handling: Thiếu `@RestControllerAdvice`

Hiện tại `CourseServiceImpl.getCourseDetail` ném `IllegalStateException` — không có global handler nên Spring trả về HTML. Cần:
- `GlobalExceptionHandler.java` với `@RestControllerAdvice`
- `ResourceNotFoundException.java` (custom exception thay thế `IllegalStateException`)

---

## PHẦN 4: PROMPT CHUẨN CHO GIAI ĐOẠN TIẾP THEO

```
# [PHASE 4] PROMPT GIAO VIỆC: BỔ SUNG LUỒNG GHI & CURRICULUM TREE — MODULE COURSE

## CONTEXT & RÀNG BUỘC TUYỆT ĐỐI

Stack: Spring Boot 4.1.0 · Java 21 · Spring Security OAuth2 JWT · Spring Data JPA · Jakarta Validation · Lombok

Bạn đang làm việc trên project `lms-backend` kiến trúc Modular Monolith. Các thành phần sau ĐÃ BUILD XONG và TUYỆT ĐỐI KHÔNG ĐƯỢC VIẾT LẠI:
- common/: ApiResponse, AppJwtProperties, JwtConfig, SecurityConfig, BaseEntity, AuditableEntity, CustomAccessDeniedHandler, CustomAuthenticationEntryPoint
- auth/: Toàn bộ Entity (User, Role, Permission, RefreshToken), Repository, DTO, Service, Controller
- course/entity/: Course.java, Module.java, Lesson.java, LessonResource.java, Category.java — CHỈ ĐƯỢC THÊM annotation, không xóa field hiện có
- course/repository/CourseRepository.java — đã có findBySlug() + findAllByStatus()
- course/dto/CourseResponse.java — đã có
- course/service/CourseService.java + CourseServiceImpl.java — đã có getAllPublishedCourses() và getCourseDetail(), chỉ được THÊM method

## NHIỆM VỤ CỤ THỂ (CHỈ CODE, KHÔNG HỎI LẠI)

### BƯỚC 1: Bổ sung @OneToMany vào các Entity hiện có
- Course.java: Thêm private List<Module> modules với @OneToMany(mappedBy="course", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY) + @JsonIgnore + @Builder.Default init ArrayList<>()
- Module.java: Thêm private List<Lesson> lessons tương tự
- Lesson.java: Thêm private List<LessonResource> resources tương tự

### BƯỚC 2: Tạo mới 4 Repository
- ModuleRepository: findAllByCourseIdOrderBySortOrder(Long courseId)
- LessonRepository: findAllByModuleIdOrderBySortOrder(Long moduleId)
- LessonResourceRepository: findAllByLessonId(Long lessonId)
- CategoryRepository: findBySlug(String slug), findAllByParentIsNull()

### BƯỚC 3: Tạo mới 6 DTO class
Request DTO (với Jakarta Validation):
- CourseUpsertRequest: title(@NotBlank @Size(max=255)), summary(@Size(max=500)), description, price(@DecimalMin("0.00")), level(@NotNull CourseLevel), categoryId, thumbnailUrl
- ModuleUpsertRequest: title(@NotBlank), sortOrder(@Min(0))
- LessonUpsertRequest: title(@NotBlank), content, videoUrl, durationSeconds(@Min(0)), lessonType(@NotNull LessonType), isPreview, sortOrder(@Min(0))
Response DTO:
- LessonResponse: id, title, lessonType(String), durationSeconds, isPreview, sortOrder
- ModuleResponse: id, title, sortOrder, List<LessonResponse> lessons
- CourseCurriculumResponse: id, title, slug, List<ModuleResponse> modules

### BƯỚC 4: Bổ sung 10 method vào CourseService interface và CourseServiceImpl
Kế thừa nguyên vẹn mapToResponse() hiện có, thêm:
- createCourse(CourseUpsertRequest request, Long instructorId) -> CourseResponse
- updateCourse(Long id, CourseUpsertRequest request) -> CourseResponse
- deleteCourse(Long id) -> void
- getCurriculum(Long courseId) -> CourseCurriculumResponse
- addModule(Long courseId, ModuleUpsertRequest request) -> ModuleResponse
- updateModule(Long moduleId, ModuleUpsertRequest request) -> ModuleResponse
- deleteModule(Long moduleId) -> void
- addLesson(Long moduleId, LessonUpsertRequest request) -> LessonResponse
- updateLesson(Long lessonId, LessonUpsertRequest request) -> LessonResponse
- deleteLesson(Long lessonId) -> void

### BƯỚC 5: Bổ sung Endpoint vào CourseController (KHÔNG xóa 2 endpoint đã có)
- POST /api/courses -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- PUT /api/courses/{id} -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- DELETE /api/courses/{id} -> @PreAuthorize("hasRole('ADMIN')")
- GET /api/courses/{id}/curriculum -> authenticated only
- POST /api/courses/{courseId}/modules -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- PUT /api/courses/modules/{moduleId} -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- DELETE /api/courses/modules/{moduleId} -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- POST /api/courses/modules/{moduleId}/lessons -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- PUT /api/courses/lessons/{lessonId} -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
- DELETE /api/courses/lessons/{lessonId} -> @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")

### BƯỚC 6: Security + Exception Handler
- Thêm @EnableMethodSecurity vào SecurityConfig
- Cập nhật authorizeHttpRequests: GET /api/courses và GET /api/courses/** là permitAll()
- Tạo ResourceNotFoundException.java trong common/exception/
- Tạo GlobalExceptionHandler.java trong common/exception/ với @RestControllerAdvice:
  * ResourceNotFoundException -> HTTP 404
  * IllegalArgumentException -> HTTP 400
  * MethodArgumentNotValidException -> HTTP 422 với danh sách field errors
  * AccessDeniedException -> HTTP 403
- Thay IllegalStateException trong CourseServiceImpl bằng ResourceNotFoundException

## YÊU CẦU BẮT BUỘC
- @Transactional cho write operations, @Transactional(readOnly=true) cho read operations
- @Valid trên tất cả @RequestBody của write endpoints
- Lombok @Builder, @Getter, @Setter nhất quán với code hiện tại
- Slug generation: util method lowercase + replace spaces bằng dấu gạch ngang
- Inject đủ courseRepository, moduleRepository, lessonRepository vào CourseServiceImpl
```
