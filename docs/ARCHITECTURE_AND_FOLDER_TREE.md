# ARCHITECTURE & DATA FLOW - LMS PROJECT

Tài liệu này mô tả kiến trúc tổng thể, cấu trúc thư mục chuẩn và luồng dữ liệu end-to-end của hệ thống LMS.

---

## 1. Cây thư mục Backend (Spring Boot 4 - Package-by-Feature)

Dự án áp dụng mô hình Package-by-Feature kết hợp Layered để tối ưu sự gắn kết (High Cohesion) và dễ dàng phân chia task cho các thành viên trong team.

    lms-backend/
    ├── src/main/java/com/lms/
    │   ├── LmsApplication.java
    │   │
    │   ├── common/                    # Các cấu hình và tiện ích dùng chung
    │   │   ├── config/                # OpenApiConfig, CorsConfig, SecurityConfig
    │   │   ├── exception/             # GlobalExceptionHandler, ResourceNotFoundException
    │   │   ├── payload/               # ApiResponse, PaginationResponse
    │   │   └── util/                  # SecurityUtils, DateTimeUtils
    │   │
    │   ├── security/                  # Module bảo mật riêng
    │   │   ├── jwt/                   # JwtTokenProvider, JwtAuthenticationFilter
    │   │   └── service/               # CustomUserDetailsService
    │   │
    │   ├── modules/                   # Các module nghiệp vụ chính (Feature-based)
    │   │   ├── auth/                  # Module Xác thực & Phân quyền
    │   │   │   ├── controller/        # AuthController
    │   │   │   ├── dto/               # LoginRequest, RegisterRequest, AuthResponse
    │   │   │   └── service/           # AuthService, AuthServiceImpl
    │   │   │
    │   │   ├── user/                  # Module Quản lý người dùng (Học viên, Giảng viên)
    │   │   │   ├── controller/        # UserController
    │   │   │   ├── dto/               # UserDto, UpdateProfileRequest
    │   │   │   ├── entity/            # User, Role, Permission
    │   │   │   ├── mapper/            # UserMapper (MapStruct)
    │   │   │   ├── repository/        # UserRepository, RoleRepository
    │   │   │   └── service/           # UserService, UserServiceImpl
    │   │   │
    │   │   ├── course/                # Module Quản lý Khóa học & Bài giảng
    │   │   │   ├── controller/        # CourseController, ModuleController
    │   │   │   ├── dto/               # CourseResponse, CreateCourseRequest
    │   │   │   ├── entity/            # Course, CourseModule, Lesson
    │   │   │   ├── repository/        # CourseRepository, LessonRepository
    │   │   │   └── service/           # CourseService
    │   │   │
    │   │   ├── quiz/                  # Module Bài kiểm tra & Trắc nghiệm
    │   │   └── enrollment/            # Module Đăng ký học & Tiến độ
    │   │
    └── src/main/resources/
        ├── application.yml            # File cấu hình chung & Active profile
        ├── application-dev.yml        # Cấu hình cho local dev
        ├── application-prod.yml       # Cấu hình cho production
        └── db/
            └── migration/             # Script Flyway migration
                ├── V1__init_users_roles_schema.sql
                ├── V2__create_courses_and_lessons_table.sql
                └── V3__add_enrollments_table.sql

---

## 2. Cây thư mục Frontend (React 18 + Vite + TS)

Áp dụng cấu trúc phân chia theo tính năng (Feature-driven architecture) để dự án không bị phình to và dễ bảo trì khi mở rộng scale.

    lms-frontend/
    ├── public/                        # Static assets (favicon, logo, robots.txt)
    ├── src/
    │   ├── assets/                    # Hình ảnh, font chữ, style chung
    │   │   ├── images/
    │   │   └── styles/
    │   │
    │   ├── components/                # UI Components dùng chung (Dumb/Shared components)
    │   │   ├── common/                # Button, Input, Modal, Table, Spinner
    │   │   └── layout/                # Navbar, Sidebar, Footer, MainLayout
    │   │
    │   ├── config/                    # Cấu hình app, hằng số, menu navigation
    │   │   └── constants.ts
    │   │
    │   ├── features/                  # Các module chức năng chính (Smart components)
    │   │   ├── auth/                  # Tính năng Auth
    │   │   │   ├── components/        # LoginForm, RegisterForm
    │   │   │   ├── hooks/             # useLogin, useRegister
    │   │   │   ├── services/          # authApi.ts
    │   │   │   └── types/             # auth.types.ts
    │   │   │
    │   │   ├── courses/               # Tính năng Khóa học
    │   │   │   ├── components/        # CourseCard, CourseList, CourseDetail
    │   │   │   ├── hooks/             # useCourses, useCourseDetail (React Query)
    │   │   │   ├── services/          # courseApi.ts
    │   │   │   └── types/             # course.types.ts
    │   │   │
    │   │   ├── dashboard/             # Trang tổng quan cho Student/Instructor
    │   │   └── quiz/                  # Làm bài kiểm tra
    │   │
    │   ├── hooks/                     # Custom React Hooks dùng chung toàn app
    │   │   ├── useDebounce.ts
    │   │   └── useOnClickOutside.ts
    │   │
    │   ├── routes/                    # Quản lý Routing & Protected Routes
    │   │   ├── AppRoutes.tsx
    │   │   └── ProtectedRoute.tsx
    │   │
    │   ├── services/                  # Global APIs & Axios Interceptor
    │   │   └── apiClient.ts           # Axios instance đã cấu hình Interceptor
    │   │
    │   ├── store/                     # Global State (Zustand / Redux Toolkit)
    │   │   └── useAuthStore.ts
    │   │
    │   ├── types/                     # Các TypeScript interfaces/types chung
    │   │   └── common.types.ts        # ApiResponse, PaginatedList
    │   │
    │   ├── utils/                     # Helper functions
    │   │   ├── formatters.ts          # Format tiền tệ, ngày tháng (date-fns)
    │   │   └── validators.ts
    │   │
    │   ├── App.tsx                    # Root Component & Providers
    │   ├── main.tsx                   # Entry point
    │   └── index.css                  # Tailwind directives
    │
    ├── .env                           # Biến môi trường local
    ├── .env.example                   # Mẫu biến môi trường
    ├── index.html
    ├── package.json
    ├── tailwind.config.js
    ├── tsconfig.json
    └── vite.config.ts

---

## 3. Luồng dữ liệu (End-to-End Data Flow)

Dưới đây là luồng xử lý chuẩn khi người dùng tương tác trên UI (Ví dụ nghiệp vụ: Học viên bấm nút "Đăng ký khóa học"):

    [React UI: Button Click] 
             │
             ▼
    [React Query Hook (useEnrollCourse)] ──(Gọi hàm API)──► [courseApi.ts]
                                                                  │
                                                                  ▼
                                                         [Axios Instance (apiClient)]
                                                         (Gắn Bearer Token vào Header)
                                                                  │
                                                            (HTTP POST /api/v1/enrollments)
                                                                  │
     ┌────────────────────────────────────────────────────────────┴─────────────────────────────┐
     │ BACKEND (Spring Boot 4)                                                                  │
     │                                                                                          │
     │  [Spring Security Filter Chain]                                                          │
     │         │                                                                                │
     │         ▼                                                                                │
     │  [JwtAuthenticationFilter] ──(Validate Token)──► [SecurityContextHolder]                 │
     │         │                                                                                │
     │         ▼                                                                                │
     │  [EnrollmentController] ──(Nhận CreateEnrollmentRequest DTO)                             │
     │         │                                                                                │
     │         ▼                                                                                │
     │  [EnrollmentService] ──(Business Logic: Kiểm tra chỗ trống, điều kiện tiên quyết)     │
     │         │                                                                                │
     │         ├──► [CourseRepository] ─► (Query Hibernate/JPA) ─► [PostgreSQL Database]        │
     │         │                                                          │                     │
     │         └──► [EnrollmentRepository] ──(Save Entity)────────────────┘                     │
     │                                                                                          │
     └────────────────────────────────────────────────────────────┬─────────────────────────────┘
                                                                  │
                                                        (Trả về ApiResponse JSON)
                                                                  │
                                                                  ▼
                                                         [Axios Interceptor]
                                                         (Phân tích HTTP Status 200/400/401)
                                                                  │
                                                                  ▼
                                                       [React Query Cache update]
                                                                  │
                                                                  ▼
                                                  [React UI Re-render / Toast Notify]

### Chi tiết các bước diễn ra trong luồng:

1. **User Interaction:** Học viên click vào nút *"Đăng ký ngay"* trên trang chi tiết khóa học (`CourseDetail.tsx`).
2. **State Management & Fetching:** Component gọi mutation hook `useEnrollCourse()` từ TanStack React Query.
3. **Axios Interceptor:** Hàm từ `courseApi.ts` sử dụng `apiClient`. Trước khi request rời trình duyệt, **Axios Request Interceptor** tự động lấy `access_token` từ LocalStorage và gắn vào header: `Authorization: Bearer <token>`.
4. **Backend Security Layer:** Request đến HTTP port `8080`. `JwtAuthenticationFilter` của Spring Security chặn lại, parse JWT, xác thực chữ ký (secret key), kiểm tra hạn sử dụng và quyền (`ROLE_STUDENT`). Nếu hợp lệ, thông tin User được nạp vào `SecurityContext`.
5. **Controller Layer:** `EnrollmentController` tiếp nhận request tại endpoint `POST /api/v1/enrollments`. Nó map JSON body thành `CreateEnrollmentRequest` DTO và kiểm tra validate (`@Valid`).
6. **Service Layer:** Controller gọi `EnrollmentService.enrollStudent(...)`. Service thực hiện business logic: kiểm tra học viên đã đăng ký chưa, khóa học có bị khóa hay hết chỗ không.
7. **Persistence Layer (Repository + DB):** Service gọi `EnrollmentRepository.save(enrollmentEntity)`. Hibernate/JPA tự động dịch Entity thành câu lệnh SQL `INSERT INTO enrollments ...` và thực thi dưới database PostgreSQL (cấu trúc DB đã được định hình chuẩn qua Flyway).
8. **Response Return:** Database trả về kết quả thành công. Service chuyển đổi `EnrollmentEntity` sang `EnrollmentResponse` DTO. Controller bọc DTO này trong `ApiResponse.success(...)` và trả về HTTP Status `200 OK`.
9. **Frontend Handling:** **Axios Response Interceptor** nhận status `200`, bóc tách lấy data từ wrapper. React Query tự động cập nhật lại Cache, trigger re-render giao diện chuyển nút "Đăng ký ngay" thành "Vào học" và hiển thị Toast thông báo thành công! *(Nếu gặp lỗi 401 Unauthorized, Interceptor sẽ tự động tạm dừng request, gọi API refresh token, và thử lại request cũ một cách ngầm định).*