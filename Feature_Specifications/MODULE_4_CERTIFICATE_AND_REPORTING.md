# MODULE 4: CERTIFICATE GENERATION & REPORTING DASHBOARD

## 1. Mục tiêu Module & Kết quả mong đợi
* **Mục tiêu nghiệp vụ:**
  * Tự động hóa hoàn toàn việc cấp chứng chỉ tốt nghiệp (PDF Certificate) có mã xác thực duy nhất cho Học viên ngay khi họ hoàn thành 100% khóa học. Cung cấp trang tra cứu/xác thực chứng chỉ công khai cho nhà tuyển dụng.
  * Xây dựng hệ thống Báo cáo & Thống kê (Analytics Dashboard) trực quan, hiệu năng cao cho Giảng viên (theo dõi doanh thu, số lượng học viên của khóa học mình) và Admin (toàn cảnh hệ thống).
* **Kết quả mong đợi:**
  * Chuyển đổi file HTML/Template sang PDF chất lượng cao, hỗ trợ chuẩn xác Font chữ tiếng Việt, xử lý hoàn toàn bất đồng bộ không gây nghẽn Server.
  * Các API Dashboard có thời gian phản hồi nhanh (dưới 300ms) ngay cả khi query trên tập dữ liệu hàng triệu record nhờ áp dụng Caching và Tối ưu SQL.

## 2. Workflow (Luồng hoạt động)

### 2.1. Luồng Tự động cấp & Xác thực Chứng chỉ (Async Certificate Flow)
1. **Event Listener:** `CourseCompletedListener` (Spring Component) bắt được sự kiện `CourseCompletedEvent` từ Module 3.
2. **Validation & Metadata Generation:** Kiểm tra xem chứng chỉ đã được cấp cho user/course này chưa. Nếu chưa -> Tạo một record trong bảng `certificates` với một `verification_code` duy nhất (VD: `CERT-2026-X89A2`).
3. **PDF Rendering (Virtual Threads / Async Pool):**
   * Service load template HTML (Sử dụng **Thymeleaf**).
   * Inject các biến số: Tên học viên, Tên khóa học, Ngày hoàn thành, Mã xác thực, QR Code URL.
   * Sử dụng thư viện **OpenHTMLToPDF** (hoặc Flying Saucer) chuyển đổi HTML String thành Mảng byte PDF (`byte[]`).
4. **Cloud Storage Upload:** Upload `byte[]` PDF lên AWS S3 / Cloudflare R2 -> Nhận lại URL lưu trữ -> Cập nhật `pdf_url` vào bảng `certificates`.
5. **Public Verification (HR/Nhà tuyển dụng):** Người thứ 3 quét mã QR trên chứng chỉ hoặc truy cập `/verify-certificate?code=CERT-2026-X89A2`. Hệ thống tra cứu DB (gọi API Public) và trả về thông tin xác nhận: *"Chứng chỉ hợp lệ. Cấp cho học viên X, hoàn thành khóa học Y vào ngày Z"*.

### 2.2. Luồng Báo cáo & Thống kê Dashboard (Reporting Workflow)
1. **Request:** Admin hoặc Instructor vào trang Dashboard -> React gửi GET request tới `/api/v1/analytics/overview?timeRange=30D`.
2. **Cache Lookup:** Spring Boot kiểm tra trong **Redis Cache** (hoặc Caffeine In-memory Cache) với key `analytics:overview:30D`.
   * *Cache Hit:* Trả dữ liệu JSON từ Cache về ngay lập tức (< 10ms).
   * *Cache Miss:* Gọi xuống Repository thực thi câu lệnh SQL Aggregation (Group by, Sum, Count, Date Truncation...) từ PostgreSQL -> Lưu kết quả vào Cache với TTL = 15 phút -> Trả về cho Client.
3. **React Rendering:** Frontend nhận data -> Sử dụng **Recharts** (hoặc Chart.js) kết hợp với tính năng `useDeferredValue` của React 18 để render các biểu đồ Doanh thu, biểu đồ Học viên đăng ký mới mà không làm đơ giao diện người dùng.

## 3. Điểm kỹ thuật cốt lõi (Tech Spotlight)

### 3.1. Xử lý tác vụ nặng (PDF Generation) bằng Virtual Threads (Java 21 / Spring Boot 3.2+)
* **Vấn đề:** Việc render PDF từ HTML và upload lên Cloud S3 là tác vụ cực kỳ nặng về I/O và CPU (I/O-bound & CPU-bound). Nếu chạy trên luồng xử lý HTTP request thông thường (Tomcat Worker Thread), khi có 50 học viên hoàn thành khóa học cùng lúc, Server sẽ cạn kiệt luồng (Thread Starvation), gây ra lỗi `503 Service Unavailable` cho toàn bộ các người dùng khác đang dùng hệ thống.
* **Giải pháp trong Spring Boot 4:** Kích hoạt **Virtual Threads (Project Loom)** và cấu hình `@Async` sử dụng Executor của Virtual Threads:
```java
// Cấu hình trong AsyncConfig.java
@Bean(name = "virtualThreadTaskExecutor")
public AsyncTaskExecutor asyncTaskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
}

// Sử dụng trong CertificateService.java
@Async("virtualThreadTaskExecutor")
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleCourseCompleted(CourseCompletedEvent event) {
    // Logic sinh PDF và Upload S3 chạy trên Virtual Thread siêu nhẹ
}
```

### 3.2. Tối ưu SQL Analytics & Caching Strategy
* Với các câu query thống kê doanh thu theo tháng/ngày, tuyệt đối không dùng code Java để `findAll()` rồi lặp qua tính tổng.
* **Sử dụng PostgreSQL Date Truncation & Group By trực tiếp trong Repository:**
```java
@Query("SELECT new com.lms.dto.RevenueChartDto(FUNCTION('TO_CHAR', e.createdAt, 'YYYY-MM-DD'), SUM(c.price), COUNT(e.id)) " +
       "FROM Enrollment e JOIN e.course c " +
       "WHERE e.createdAt >= :startDate AND e.status = 'ACTIVE' " +
       "GROUP BY FUNCTION('TO_CHAR', e.createdAt, 'YYYY-MM-DD') " +
       "ORDER BY FUNCTION('TO_CHAR', e.createdAt, 'YYYY-MM-DD') ASC")
List<RevenueChartDto> getDailyRevenue(@Param("startDate") LocalDateTime startDate);
```
* Kết hợp `@Cacheable(value = "dashboard_revenue", key = "#startDate", unless = "#result == null")` để giảm tải 95% áp lực truy vấn cho PostgreSQL trong các giờ cao điểm.

## 4. Lỗi thực tế & Cách debug (Troubleshooting)

### Lỗi 1: Font tiếng Việt bị lỗi ô vuông (`???`, `□□□`) khi xuất file PDF
* **Hiện tượng:** Tên học viên tiếng Việt (VD: *Nguyễn Văn Đạt*) hiển thị cực kỳ đẹp trên template HTML/Thymeleaf, nhưng khi gen ra file PDF tải về thì biến thành các ký tự rác hoặc ô vuông.
* **Nguyên nhân:** Thư viện render PDF (OpenHTMLToPDF / Flying Saucer) chạy trên Môi trường Server (Linux/Docker) không được cài đặt sẵn các bộ font hỗ trợ Unicode/Tiếng Việt (như Arial, Roboto, Times New Roman).
* **Cách Fix:**
  1. Nhúng trực tiếp file font TTF (VD: `Roboto-Regular.ttf`, `Roboto-Bold.ttf`) vào thư mục `src/main/resources/fonts/` của dự án Spring Boot.
  2. Trong code cấu hình PDF Builder, load font này một cách tường minh vào `PdfRendererBuilder`:
```java
builder.useFont(new File(getClass().getResource("/fonts/Roboto-Regular.ttf").toURI()), "Roboto");
```
  3. Trong file HTML Template, bắt buộc khai báo CSS: `body { font-family: 'Roboto', sans-serif; }` và `<meta charset="UTF-8" />`.

### Lỗi 2: OutOfMemoryError (OOM): Java heap space khi nhiều người gen PDF cùng lúc
* **Hiện tượng:** Server bị crash hoàn toàn khi chạy chiến dịch marketing có lượng lớn học viên nhận chứng chỉ cùng lúc.
* **Nguyên nhân:** Thư viện gen PDF tạo ra các đối tượng DOM Tree và Byte Array cực lớn trong bộ nhớ Heap. Nếu không giới hạn số lượng tác vụ chạy đồng thời, bộ nhớ Heap sẽ bị tràn trước khi Garbage Collector (GC) kịp dọn dẹp.
* **Cách Fix:**
  * Sử dụng `Semaphore` hoặc cấu hình bounded queue trong `ThreadPoolTaskExecutor` (nếu không dùng Virtual Threads) để giới hạn tối đa chỉ cho phép VD: 10 tác vụ render PDF chạy đồng thời. Các tác vụ thứ 11 trở đi phải xếp hàng chờ trong Queue.
  * Tăng cường bộ nhớ Heap cho JVM trên production (`-Xms1024m -Xmx2048m`).

### Lỗi 3: Biểu đồ React Dashboard bị re-render liên tục gây đơ trình duyệt
* **Hiện tượng:** Khi Admin thay đổi bộ lọc ngày tháng trên Dashboard, toàn bộ trang web bị khựng (freeze) 1-2 giây, các animation của biểu đồ Recharts bị giật lag.
* **Nguyên nhân:** Dữ liệu trả về từ API có hàng nghìn điểm dữ liệu (Data points), React phải tính toán layout SVG quá lớn trên Main Thread đồng thời với việc cập nhật các UI control khác.
* **Cách Fix (React 18 Concurrent Mode):**
  * Sử dụng hook `useDeferredValue` cho data truyền vào Component Biểu đồ:
```typescript
const deferredChartData = useDeferredValue(rawApiData);
return <ResponsiveContainer><LineChart data="{deferredChartData}">...</LineChart></ResponsiveContainer>;
```
  * Khi filter thay đổi, React sẽ ưu tiên cập nhật UI của bộ lọc (Input/Dropdown) trước, biểu đồ sẽ được render ở background với độ ưu tiên thấp hơn, mang lại trải nghiệm mượt mà tuyệt đối cho người dùng.

## 5. Danh sách File quan trọng & API Endpoints

### 5.1. Danh sách File cốt lõi (Backend & Frontend)
```text
[Backend: Spring Boot 4]
├── domain/entity/Certificate.java         # Entity Chứng chỉ (user_id, course_id, verification_code, pdf_url)
├── repository/CertificateRepository.java  # JPA Repo cho tra cứu và verify code
├── repository/AnalyticsRepository.java    # Repository chứa các câu Native/JPQL Aggregation cho Dashboard
├── service/CertificateService.java        # Xử lý event hoàn thành khóa học, phối hợp gen PDF & Upload S3
├── service/PdfGeneratorService.java       # Engine render PDF từ Thymeleaf template & Font embedding
├── service/AnalyticsService.java          # Service tính toán số liệu thống kê (Có tích hợp @Cacheable)
├── config/AsyncConfig.java                # Cấu hình Virtual Thread Task Executor cho @Async
└── controller/AnalyticsController.java    # REST API Dashboard cho Admin và Instructor

[Frontend: React 18 TypeScript]
├── src/types/analytics.types.ts           # TypeScript interfaces (RevenueChartDto, KpiSummaryDto, CertificateDto)
├── src/components/dashboard/KpiCards.tsx    # Component hiển thị các chỉ số tổng quan (Tổng doanh thu, Tổng HV...)
├── src/components/dashboard/RevenueChart.tsx # Biểu đồ doanh thu sử dụng Recharts + useDeferredValue
└── src/components/certificate/VerifyModal.tsx # Form tra cứu & hiển thị thông tin xác thực chứng chỉ công khai
```

### 5.2. Danh sách REST API Endpoints

| Method | Endpoint | Quyền (RBAC) | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/certificates/my-certificates`| `ROLE_STUDENT` | Lấy danh sách chứng chỉ mà học viên đã đạt được kèm link tải PDF. |
| **GET** | `/api/v1/certificates/{id}/download`| `ROLE_STUDENT` | Lấy URL tải file PDF chứng chỉ (Redirection tới S3/R2 Presigned URL). |
| **GET** | `/api/v1/public/certificates/verify`| Public | Tra cứu tính hợp lệ của chứng chỉ thông qua `code` (Dành cho HR/Employer). |
| **GET** | `/api/v1/instructor/analytics/overview`| `ROLE_INSTRUCTOR`| Lấy số liệu KPI tổng quan các khóa học của Giảng viên đó (Doanh thu, HV). |
| **GET** | `/api/v1/instructor/analytics/revenue`| `ROLE_INSTRUCTOR`| Lấy dữ liệu vẽ biểu đồ doanh thu theo chu kỳ (Ngày/Tuần/Tháng). |
| **GET** | `/api/v1/admin/analytics/overview` | `ROLE_ADMIN` | Lấy số liệu KPI toàn cảnh toàn bộ nền tảng LMS. |
| **GET** | `/api/v1/admin/analytics/top-courses`| `ROLE_ADMIN` | Lấy danh sách Top 10 khóa học bán chạy nhất / điểm đánh giá cao nhất. |

## 6. Checklist triển khai & Đầu ra
- [ ] **Database Migration:** Tạo script Flyway `V4__init_certificates_and_indexing.sql`. Tạo bảng `certificates` với ràng buộc `UNIQUE(verification_code)` và `UNIQUE(user_id, course_id)`. Add Index cho các column thường dùng để group by trong Analytics (như `created_at`, `status` trong bảng `enrollments`).
- [ ] **Font & Template Testing:** Viết Unit Test `PdfGeneratorServiceTest`, chạy thử render 1 file PDF mẫu với chuỗi tên tiếng Việt dài nhất có thể (VD: *Nguyễn Hoàng Phương Mai Anh*), mở file PDF output kiểm tra mắt thường đảm bảo không lỗi font.
- [ ] **Async & Thread Pool Verification:** Kiểm tra log hệ thống khi trigger sự kiện hoàn thành khóa học, xác nhận tên luồng xử lý PDF phải là luồng ảo (VD: `VirtualThread[#23]/runnable`), không được chiếm dụng luồng HTTP TomCat (`http-nio-8080-exec-*`).
- [ ] **Cache Eviction Strategy:** Cấu hình đúng cơ chế xóa cache (`@CacheEvict`) hoặc TTL cho Redis. Đảm bảo khi có học viên vừa mua khóa học mới, số liệu trên Dashboard của Instructor sẽ được cập nhật lại chậm nhất sau 15 phút (hoặc realtime nếu áp dụng Event-driven cache eviction).
- [ ] **UI Performance Benchmark:** Test trang React Dashboard với tập dữ liệu mẫu 5,000 ngày doanh thu. Đảm bảo thao tác chuyển đổi qua lại giữa các tab trên giao diện phải đạt 60 FPS, không bị rớt khung hình nhờ `useDeferredValue`.