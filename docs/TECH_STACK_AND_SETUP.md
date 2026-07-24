# TECH STACK & SETUP GUIDE - LMS PROJECT

Tài liệu này hướng dẫn cài đặt môi trường và cấu hình chi tiết cho Backend và Frontend.

---

## 1. Yêu cầu môi trường (Prerequisites)

Để đảm bảo không bị lỗi tương thích, toàn bộ team phải cài đặt các công cụ với phiên bản tối thiểu sau:
- Java Development Kit (JDK): JDK 21 LTS (bắt buộc cho Spring Boot 4 / hệ sinh thái mới).
- Node.js & Package Manager: Node.js v20+ LTS, npm v10+ (hoặc pnpm v9+).
- Database: PostgreSQL v15+ (Local hoặc chạy qua Docker).
- IDE Khuyên dùng: IntelliJ IDEA Ultimate / Eclipse (cho Backend) & Visual Studio Code (cho Frontend).

---

## 2. Hướng dẫn cấu hình Backend (Spring Boot 4)

### 2.1. File cấu hình src/main/resources/application.yml
Tạo file cấu hình với các profile dev và prod. Dưới đây là cấu hình chuẩn cho môi trường local (dev):

    server:
      port: 8080
      servlet:
        context-path: /

    spring:
      application:
        name: lms-backend
      profiles:
        active: dev
      
      # Cấu hình kết nối PostgreSQL
      datasource:
        url: jdbc:postgresql://localhost:5432/lms_db
        username: ${DB_USERNAME:postgres}
        password: ${DB_PASSWORD:root}
        driver-class-name: org.postgresql.Driver
        hikari:
          maximum-pool-size: 10
          minimum-idle: 5

      # Cấu hình JPA / Hibernate
      jpa:
        hibernate:
          ddl-auto: validate # Tuyệt đối KHÔNG dùng 'update' hay 'create' trong dự án có Flyway
        show-sql: true
        properties:
          hibernate:
            format_sql: true
            dialect: org.hibernate.dialect.PostgreSQLDialect

      # Cấu hình Flyway Migration
      flyway:
        enabled: true
        baseline-on-migrate: true
        locations: classpath:db/migration
        user: ${spring.datasource.username}
        password: ${spring.datasource.password}

    # Cấu hình bảo mật JWT
    app:
      jwt:
        secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970} # Base64 256-bit
        expiration-ms: 3600000 # 1 giờ
        refresh-expiration-ms: 604800000 # 7 ngày

    # Cấu hình Swagger / OpenAPI (Springdoc)
    springdoc:
      api-docs:
        path: /v3/api-docs
      swagger-ui:
        path: /swagger-ui.html
        tags-sorter: alpha
        operations-sorter: alpha

### 2.2. Quy tắc Flyway Migration
- Thư mục chứa script: src/main/resources/db/migration/.
- Định dạng đặt tên file: V<Phiên_bản>__<Mô_tả_ngắn>.sql (Lưu ý: 2 dấu gạch dưới __).
- Ví dụ chuẩn: V1__init_schema.sql, V2__add_role_column_to_users.sql.
- Luật bất thành văn: Không bao giờ được sửa file migration .sql đã được merge và chạy trên môi trường chung. Muốn thay đổi DB, buộc phải tạo file V<Next>...sql mới.

---

## 3. Hướng dẫn cấu hình Frontend (Vite + React 18 + TS)

### 3.1. Khởi tạo dự án và cấu hình .env
Dùng Vite để tạo project (nếu chưa có):

    npm create vite@latest lms-frontend -- --template react-ts
    cd lms-frontend && npm install

Tạo file .env ở thư mục gốc (và .env.example để commit lên Git):

    VITE_API_BASE_URL=http://localhost:8080/api/v1
    VITE_APP_TITLE=LMS Portal

### 3.2. Cấu hình Axios Interceptor (Xử lý JWT & Auto Refresh Token 401)
Tạo file src/services/apiClient.ts:

    import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

    const apiClient = axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true, // Cho phép gửi HttpOnly Cookie (chứa Refresh Token nếu dùng)
    });

    // Request Interceptor: Tự động đính kèm Access Token vào Header
    apiClient.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const accessToken = localStorage.getItem('access_token');
        if (accessToken && config.headers) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response Interceptor: Tự động Refresh Token khi gặp lỗi 401 Unauthorized
    let isRefreshing = false;
    let failedQueue: Array<{
      resolve: (value?: unknown) => void;
      reject: (reason?: unknown) => void;
    }> = [];

    const processQueue = (error: AxiosError | null, token: string | null = null) => {
      failedQueue.forEach((prom) => {
        if (error) {
          prom.reject(error);
        } else {
          prom.resolve(token);
        }
      });
      failedQueue = [];
    };

    apiClient.interceptors.response.use(
      (response) => response.data, // Trả về thẳng data trong ApiResponse wrapper
      async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        if (error.response?.status === 401 && !originalRequest._retry) {
          if (isRefreshing) {
            return new Promise((resolve, reject) => {
              failedQueue.push({ resolve, reject });
            })
              .then((token) => {
                if (originalRequest.headers) {
                  originalRequest.headers.Authorization = `Bearer ${token}`;
                }
                return apiClient(originalRequest);
              })
              .catch((err) => Promise.reject(err));
          }

          originalRequest._retry = true;
          isRefreshing = true;

          try {
            const refreshToken = localStorage.getItem('refresh_token');
            const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/auth/refresh-token`, {
              refreshToken,
            });

            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data.data;
            localStorage.setItem('access_token', newAccessToken);
            if (newRefreshToken) localStorage.setItem('refresh_token', newRefreshToken);

            apiClient.defaults.headers.common.Authorization = `Bearer ${newAccessToken}`;
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            }

            processQueue(null, newAccessToken);
            return apiClient(originalRequest);
          } catch (refreshError) {
            processQueue(refreshError as AxiosError, null);
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');
            window.location.href = '/login'; // Redirect về login khi refresh token hết hạn
            return Promise.reject(refreshError);
          } finally {
            isRefreshing = false;
          }
        }

        return Promise.reject(error);
      }
    );

    export default apiClient;

### 3.3. Cấu hình TailwindCSS
Cài đặt Tailwind và autoprefixer qua npm:

    npm install -D tailwindcss postcss autoprefixer
    npx tailwindcss init -p

Cấu hình tailwind.config.js:

    /** @type {import('tailwindcss').Config} */
    export default {
      content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
      ],
      theme: {
        extend: {
          colors: {
            primary: {
              50: '#f0f9ff',
              500: '#0ea5e9',
              600: '#0284c7',
              700: '#0369a1',
            },
          },
        },
      },
      plugins: [],
    }

Thêm directives vào file src/index.css:

    @tailwind base;
    @tailwind components;
    @tailwind utilities;