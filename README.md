# 🎓 Hệ Thống Quản Lý Học Tập Trực Tuyến (LMS)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot%204-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React%2018-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL%2015-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

**Một nền tảng quản lý đào tạo và học tập trực tuyến hiện đại, hiệu năng cao, được xây dựng theo kiến trúc Enterprise Micro-monolith.**

---

## 📖 Giới Thiệu Dự Án

**LMS (Learning Management System) — Đề tài 8** là giải pháp phần mềm được phát triển nhằm tự động hóa và nâng cao trải nghiệm dạy - học trực tuyến. Hệ thống tập trung giải quyết bài toán quản lý lộ trình học tập, theo dõi tiến độ thời gian thực, và tối ưu hóa sự tương tác giữa Giảng viên (Instructor) và Học viên (Student).

Dự án áp dụng triệt để các Best Practices trong kỹ thuật phần mềm như: **RESTful API Design**, **Stateless Authentication với JWT**, **Database Migration với Flyway**, và **Component-based UI Architecture**.

---

## 🚀 Tính Năng Nổi Bật

### 📚 1. Quản Lý Khóa Học & Lộ Trình (Course & Curriculum Management)
* **Phân cấp linh hoạt:** Cấu trúc bài bản theo hướng Khóa học -> Chương học (Modules) -> Bài học (Lessons).
* **Đa dạng học liệu:** Hỗ trợ phát video stream trực tiếp (YouTube/Vimeo Embed, HLS stream), bài giảng dạng Text/Markdown, và tài liệu tải về (PDF, ZIP).
* **Phân quyền chặt chẽ:** Kiểm soát truy cập học liệu theo trạng thái đăng ký của học viên (Free Preview vs Enrolled).

### 📝 2. Hệ Thống Kiểm Tra & Đánh Giá (Interactive Quizzes)
* **Bộ đề trắc nghiệm thông minh:** Hỗ trợ câu hỏi Single-choice và Multi-choice với thời gian làm bài đếm ngược thời gian thực.
* **Tự động chấm điểm:** Tính toán điểm số ngay lập tức, lưu trữ lịch sử làm bài và phản hồi chi tiết giải thích cho từng đáp án.

### 📅 3. Điểm Danh & Theo Dõi Tiến Độ (Attendance & Progress Tracking)
* **Điểm danh thời gian thực:** Giảng viên tạo và theo dõi các buổi học (Sessions); ghi nhận trạng thái **PRESENT** (Có mặt), **ABSENT** (Vắng mặt), **LATE** (Muộn) kèm ghi chú.
* **Progress Bar:** Tự động tính toán % hoàn thành khóa học dựa trên số bài học đã đánh dấu hoàn tất và kết quả các bài kiểm tra.

### 🏆 4. Cấp Phát Chứng Chỉ (Certificate Generation)
* **Tự động hóa:** Cấp phát chứng chỉ số (Digital Certificate) dưới dạng PDF động ngay khi học viên hoàn thành 100% lộ trình và đạt điểm sàn yêu cầu.
* **Xác thực chứng chỉ:** Tích hợp mã QR và mã định danh duy nhất (UUID) cho phép bên thứ ba tra cứu độ xác thực của chứng chỉ.

---

## 🏗 Kỹ Thuật & Kiến Trúc Sử Dụng

| Thành phần | Công nghệ sử dụng | Chi tiết kỹ thuật |
| :--- | :--- | :--- |
| **Backend** | Spring Boot 4, Java 21 | Spring Security 6 (JWT OAuth2), Spring Data JPA, Hibernate |
| **Frontend** | React 18, TypeScript | Vite, Redux Toolkit, React Query, Tailwind CSS, Lucide Icons |
| **Database** | PostgreSQL 15 | Flyway Migration, HikariCP Connection Pooling |
| **DevOps** | Docker, Docker Compose | Multi-stage build, Nginx Reverse Proxy |

---

## ⚡ Hướng Dẫn Cài Đặt Nhanh (Quick Start)

### Yêu Cầu Hệ Thống (Prerequisites)
* **JDK 21** trở lên.
* **Node.js 18.x** trở lên & npm.
* **Docker & Docker Compose** (nếu muốn chạy nhanh DB).

### Bước 1: Khởi động Cơ sở dữ liệu
Tại thư mục gốc của dự án, khởi động PostgreSQL qua Docker:
```bash
docker-compose up -d postgres
```

### Bước 2: Khởi chạy Backend Server (Spring Boot 4)
Mở terminal tại thư mục gốc, chạy lệnh:

    ./mvnw clean spring-boot:run

> **Lưu ý:** Trong lần chạy đầu tiên, **Flyway** sẽ tự động thực thi các file migration (`V1__init_schema.sql` và `V2__insert_seed_data.sql`) để tạo bảng và nạp dữ liệu mẫu. Server chạy tại `http://localhost:8080`.

### Bước 3: Khởi chạy Frontend Client (React 18)
Mở một terminal mới, di chuyển vào thư mục frontend và khởi động server phát triển:

    cd frontend
    npm install
    npm run dev

> Truy cập ứng dụng tại `http://localhost:3000`.

---

## 🔑 Tài Khoản Kiểm Thử Mặc Định (Seed Data)

| Role | Username / Email | Password | Ghi chú |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin_super` | `123456` | Toàn quyền quản trị hệ thống |
| **Giảng viên** | `instructor_dev` | `123456` | Quản lý khóa học, điểm danh, tạo quiz |
| **Học viên** | `student_john` | `123456` | Học bài, làm quiz, xem tiến độ |

---

## 📂 Mục Lục Tài Liệu Chuyên Sâu

Để tìm hiểu chi tiết về quy chuẩn phát triển và thiết kế hệ thống, vui lòng tham khảo các tài liệu kỹ thuật đã được tôi chuẩn bị bên dưới:

* [**File 1: Coding Conventions & Standards**](./docs/CODING_CONVENTIONS.md)
* [**File 2: Environment Setup & Installation Guide**](./docs/TECH_STACK_AND_SETUP.md)
* [**File 3: System Architecture & Database Schema**](./docs/ARCHITECTURE_AND_FOLDER_TREE.md)
* [**Folder 1: Feature Specifications & API Docs**](./Feature_Specifications/)
* [**Folder 2: Database Migrations**](./db/)

---

## Dự án được thiết kế và phát triển bởi Ngô Duy Hưng - Fullstack Developer

## 📄 License

Dự án được phát hành dưới giấy phép **MIT License**. Bạn có quyền tự do sử dụng, chỉnh sửa và phân phối cho mục đích học tập và thương mại. Xem chi tiết tại file [LICENSE](./LICENSE).