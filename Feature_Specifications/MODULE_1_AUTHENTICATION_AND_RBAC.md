# MODULE 1: AUTHENTICATION & ROLE-BASED ACCESS CONTROL (RBAC)

## 1. Mục tiêu Module & Kết quả mong đợi
* **Mục tiêu nghiệp vụ:** Xây dựng hệ thống định danh, xác thực (Authentication) và phân quyền (Authorization) bảo mật cao cho 3 nhóm người dùng cốt lõi: **STUDENT** (Học viên), **INSTRUCTOR** (Giảng viên), **ADMIN** (Quản trị viên).
* **Kết quả mong đợi:**
  * Hệ thống xác thực không trạng thái (Stateless Authentication) sử dụng **JSON Web Token (JWT)**.
  * Cơ chế **Refresh Token Rotation** chống tấn công Replay Attack và đánh cắp Token, hỗ trợ thu hồi quyền truy cập (Revoke/Blacklist) tức thì.
  * Phân quyền chặt chẽ đến từng API Endpoint (Backend) và từng UI Component/Route (Frontend).

## 2. Workflow (Luồng hoạt động)

### 2.1. Luồng đăng nhập & Cấp phát Token (Happy Path)
1. **Client:** Người dùng nhập `email/password` trên React Form -> Gửi POST request tới `/api/v1/auth/login`.
2. **Controller & Security Filter:** `AuthController` tiếp nhận -> Gọi `AuthService`.
3. **Service & Database:** Kiểm tra thông tin trong PostgreSQL qua `UserRepository` -> Kiểm tra mật khẩu bằng `BCryptPasswordEncoder`.
4. **Token Generation:** Nếu hợp lệ, `JwtTokenProvider` tạo ra 2 token:
   * **Access Token (JWT):** Thời gian sống ngắn (15 - 30 phút), chứa claims (`sub: userId`, `roles: ["ROLE_STUDENT"]`).
   * **Refresh Token:** Thời gian sống dài (7 - 30 ngày), là một chuỗi UUID ngẫu nhiên được lưu vào bảng `refresh_tokens` trong DB kèm thời gian hết hạn (`expires_at`) và trạng thái (`revoked = false`).
5. **Response:** Trả Access Token trong Response Body (để React lưu vào Memory/TanStack Query State) và Refresh Token trong **HttpOnly, Secure, SameSite=Strict Cookie** (chống XSS).

### 2.2. Luồng Refresh Token Rotation (Happy Path & Edge Cases)
1. **Expired Access Token:** Khi Access Token hết hạn, API trả về HTTP Status `401 Unauthorized`.
2. **Axios Interceptor (React):** Interceptor bắt lỗi 401 -> Tự động tạm dừng các request đang chờ (Request Queue) -> Gửi POST `/api/v1/auth/refresh-token` (tự động đính kèm HttpOnly Cookie).
3. **Backend Rotation Logic (@Transactional):**
   * Tìm Refresh Token trong DB. Kiểm tra `expires_at` và `revoked`.
   * *Edge Case (Tấn công bảo mật - Replay Attack):* Nếu Refresh Token **đã bị thu hồi (revoked = true)** mà vẫn được mang đi dùng -> Cảnh báo rò rỉ -> **Lập tức thu hồi toàn bộ Refresh Token của User đó trong DB** -> Buộc User đăng nhập lại từ đầu.
   * *Happy Path:* Nếu hợp lệ -> Đánh dấu token hiện tại là `revoked = true` -> Tạo cặp Access Token + Refresh Token mới -> Lưu Refresh Token mới vào DB -> Trả về cho Client.
4. **Client Resume:** Axios Interceptor cập nhật Access Token mới -> Gửi lại các request bị lỗi 401 trước đó.

## 3. Điểm kỹ thuật cốt lõi (Tech Spotlight)

### 3.1. Spring Security 6.x Lambda DSL & Stateless Architecture
* Trong Spring Boot 3.x/4, `WebSecurityConfigurerAdapter` đã bị loại bỏ hoàn toàn. Bắt buộc cấu hình qua Bean `SecurityFilterChain`.
* Sử dụng `@EnableMethodSecurity` để kích hoạt phân quyền mức method: `@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")`.
* **Thiết lập Stateless:**
```java
http.sessionManagement(session -> session.creationPolicy(SessionCreationPolicy.STATELESS))
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/auth/**", "/api/v1/public/**").permitAll()
        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    )
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

### 3.2. React 18 & Axios Interceptor Concurrency Handling
* **Vấn đề:** Khi trang Dashboard gọi đồng thời 5 APIs và Access Token hết hạn, cả 5 request đều trả về 401. Nếu không xử lý khéo, React sẽ gọi `/refresh-token` 5 lần liên tiếp gây lỗi Race Condition ở Backend.
* **Giải pháp (Mutex/Promise Queue trong Axios Interceptor):**
  * Tạo cờ `isRefreshing = false` và một mảng `failedQueue = []`.
  * Khi request đầu tiên bị 401 -> Đặt `isRefreshing = true` -> Gọi API refresh.
  * 4 request tiếp theo bị 401 trong lúc `isRefreshing == true` -> Đẩy vào `failedQueue` (dưới dạng Promise).
  * Khi refresh thành công -> Duyệt `failedQueue`, resolve các Promise với token mới -> Các request tự động chạy tiếp.

## 4. Lỗi thực tế & Cách debug (Troubleshooting)

### Lỗi 1: CORS Policy & HttpOnly Cookie không được gửi đi
* **Hiện tượng:** Frontend gọi `/api/v1/auth/login` thành công nhưng khi gọi `/api/v1/auth/refresh-token` thì Backend báo lỗi không tìm thấy Cookie, hoặc trình duyệt chặn request vì lỗi CORS.
* **Nguyên nhân:** Backend cấu hình `allowedOrigins("*")` đi kèm với `allowCredentials(true)` (trình duyệt cấm bảo mật này). Frontend Axios chưa bật `withCredentials: true`.
* **Cách Fix:**
  * Backend Spring Boot `CorsConfiguration`: Phải chỉ định rõ Origin (VD: `http://localhost:5173`), tuyệt đối không dùng `*`. Cài đặt `corsConfig.setAllowCredentials(true);`.
  * Frontend Axios: `axiosInstance.defaults.withCredentials = true;`.

### Lỗi 2: Infinite Loop (Vòng lặp vô tận) trong Axios Interceptor
* **Hiện tượng:** Trình duyệt treo, Network tab nháy liên tục các call `/refresh-token` và API nghiệp vụ cho đến khi tràn bộ nhớ.
* **Nguyên nhân:** Bản thân API `/refresh-token` cũng trả về 401 (khi Refresh Token hết hạn), Interceptor lại bắt cờ 401 và tiếp tục gọi lại chính API `/refresh-token`.
* **Cách Fix:** Thêm thuộc tính `_retry` vào config của request.
```typescript
if (error.response?.status === 401 && !originalRequest._retry && originalRequest.url !== '/api/v1/auth/refresh-token') {
    originalRequest._retry = true;
    // Thực hiện logic refresh token...
} else {
    // Nếu refresh token cũng lỗi 401 -> Clear state, Redirect về /login
    window.location.href = '/login';
    return Promise.reject(error);
}
```

### Lỗi 3: LazyInitializationException khi Load User & Role trong Spring Security
* **Hiện tượng:** Khi `CustomUserDetailsService` load User từ DB để đưa vào `SecurityContextHolder`, hệ thống ném lỗi `org.hibernate.LazyInitializationException: could not initialize proxy - no Session`.
* **Nguyên nhân:** Entity `User` có quan hệ `@ManyToMany(fetch = FetchType.LAZY)` với Entity `Role`. Khi ra khỏi scope của Repository method, Hibernate Session đóng lại, Spring Security gọi `user.getRoles()` dẫn đến lỗi.
* **Cách Fix:** Sử dụng `@EntityGraph` trong Repository hoặc dùng câu lệnh `JOIN FETCH`.
```java
@EntityGraph(attributePaths = {"roles"})
Optional<User> findByEmail(String email);
```

## 5. Danh sách File quan trọng & API Endpoints

### 5.1. Danh sách File cốt lõi (Backend & Frontend)
```text
[Backend: Spring Boot 4]
├── domain/entity/User.java                # Entity User (id, email, password, status...)
├── domain/entity/Role.java                # Entity Role (id, name: ADMIN/INSTRUCTOR/STUDENT)
├── domain/entity/RefreshToken.java        # Entity Refresh Token (token, expiry, revoked, user_id)
├── repository/UserRepository.java         # JPA Repository có @EntityGraph load Roles
├── security/JwtTokenProvider.java         # Utility tạo, parse và validate JWT
├── security/JwtAuthenticationFilter.java  # OncePerRequestFilter kiểm tra Header Authorization
├── security/SecurityConfig.java           # Cấu hình SecurityFilterChain & CORS
├── service/AuthService.java               # Logic Login, Register, Refresh Token (@Transactional)
└── controller/AuthController.java         # REST Endpoints cho Authentication

[Frontend: React 18 TypeScript]
├── src/types/auth.types.ts                # TypeScript interfaces (User, TokenResponse, LoginPayload)
├── src/api/axiosClient.ts                 # Cấu hình Axios instance + Interceptors xử lý Refresh Queue
├── src/context/AuthContext.tsx            # React Context / Zustand store quản lý Auth State
└── src/components/guard/ProtectedRoute.tsx # HOC/Component bảo vệ Route theo Role
```

### 5.2. Danh sách REST API Endpoints

| Method | Endpoint | Quyền (RBAC) | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/auth/register` | Public | Đăng ký tài khoản mới (Mặc định role STUDENT). |
| **POST** | `/api/v1/auth/login` | Public | Xác thực email/password. Trả về Access Token + Refresh Cookie. |
| **POST** | `/api/v1/auth/refresh-token` | Public (kèm Cookie) | Thu hồi Refresh Token cũ, cấp cặp Access/Refresh Token mới. |
| **POST** | `/api/v1/auth/logout` | Authenticated | Thu hồi (Revoke) Refresh Token trong DB, xóa Cookie. |
| **GET** | `/api/v1/users/me` | Authenticated | Lấy thông tin Profile và Roles của user đang đăng nhập. |
| **PUT** | `/api/v1/admin/users/{id}/role` | `ROLE_ADMIN` | Nâng cấp user lên `ROLE_INSTRUCTOR` hoặc hạ quyền. |

## 6. Checklist triển khai & Đầu ra
- [ ] **Database Migration:** Tạo script Flyway `V1__init_auth_tables.sql` (bảng `users`, `roles`, `user_roles`, `refresh_tokens` với indexes trên column `email` và `token`).
- [ ] **Backend Security:** Implement `JwtAuthenticationFilter` vượt qua các test cases: Token hết hạn, Token chữ ký sai, Request không có token.
- [ ] **Backend RBAC:** Viết Integration Test kiểm chứng `@PreAuthorize` hoạt động đúng (STUDENT gọi API của ADMIN phải nhận HTTP 403 Forbidden).
- [ ] **Frontend Axios:** Test giả lập mạng chậm và Access Token hết hạn, đảm bảo Mutex Queue trong Axios Interceptor chỉ gọi API `/refresh-token` đúng 1 lần duy nhất khi có nhiều request đồng thời.
- [ ] **Frontend Guard:** Hoàn thiện `ProtectedRoute`, tự động redirect về `/login` nếu chưa chứng thực, hoặc trang `403 Unauthorized` nếu sai Role.