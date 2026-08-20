# JWT Refresh Token Flow với Axios Interceptor

## 1. Bài toán

Trong ứng dụng React, sau khi người dùng đăng nhập thành công:

* Server cấp Access Token
* Server cấp Refresh Token

Ví dụ:

```json
{
  "accessToken": "access_abc",
  "refreshToken": "refresh_xyz"
}
```

Thông thường:

* Access Token sống ngắn (15 phút)
* Refresh Token sống dài hơn (7 ngày)

Mục tiêu:

* Người dùng không phải đăng nhập lại sau mỗi 15 phút
* Hệ thống tự động lấy Access Token mới khi token cũ hết hạn

---

## 2. Tại sao không dùng 1 Access Token duy nhất?

Ví dụ:

```text
Access Token hết hạn sau 15 phút
```

Sau 15 phút:

```http
GET /users

401 Unauthorized
```

Nếu không có Refresh Token:

```text
=> Bắt người dùng login lại
```

Trải nghiệm người dùng rất tệ.

---

## 3. Giải pháp Refresh Token

Khi Access Token hết hạn:

```http
GET /users

401 Unauthorized
```

Frontend gọi:

```http
POST /auth/refresh-token
```

Body:

```json
{
  "refreshToken": "refresh_xyz"
}
```

Server kiểm tra Refresh Token.

Nếu hợp lệ:

```json
{
  "accessToken": "new_access",
  "refreshToken": "new_refresh"
}
```

Frontend lưu token mới.

Sau đó gọi lại request ban đầu.

Người dùng hoàn toàn không nhận ra điều gì xảy ra.

---

# 4. Luồng thực tế

Giả sử:

```text
Access Token đã hết hạn
Refresh Token vẫn còn hiệu lực
```

Người dùng mở trang Dashboard.

Frontend gọi:

```http
GET /users
```

Header:

```http
Authorization: Bearer access_old
```

Server trả:

```http
401 Unauthorized
```

---

Frontend phát hiện:

```text
Token hết hạn
```

Gọi:

```http
POST /auth/refresh-token
```

Body:

```json
{
  "refreshToken": "refresh_xyz"
}
```

Server trả:

```json
{
  "accessToken": "access_new",
  "refreshToken": "refresh_new"
}
```

Frontend lưu:

```text
access_new
refresh_new
```

Sau đó tự động gọi lại:

```http
GET /users
```

Header:

```http
Authorization: Bearer access_new
```

Lần này:

```http
200 OK
```

Dữ liệu hiển thị bình thường.

---

# 5. Vấn đề khi có nhiều request cùng lúc

Giả sử Dashboard gọi:

```text
GET /users
GET /courses
GET /students
GET /profile
GET /notifications
```

Cả 5 request đều dùng token đã hết hạn.

Server trả:

```text
401
401
401
401
401
```

Nếu xử lý ngây thơ:

```text
/users -> refresh
/courses -> refresh
/students -> refresh
/profile -> refresh
/notifications -> refresh
```

Kết quả:

```text
5 request refresh-token
```

Đây là điều không mong muốn.

---

# 6. Queue giải quyết vấn đề gì?

Ý tưởng:

```text
Chỉ cho phép 1 request refresh token chạy
```

Các request còn lại:

```text
Đứng chờ
```

Khi refresh thành công:

```text
Thông báo cho toàn bộ request đang chờ
```

Sau đó tất cả retry lại.

---

Ví dụ:

Request đầu tiên:

```text
/users
```

vào interceptor.

Biến:

```ts
isRefreshing = false
```

Nó sẽ:

```ts
isRefreshing = true
```

và bắt đầu refresh token.

---

Trong lúc đó:

```text
/notifications
/profile
/students
```

cũng bị 401.

Nhưng:

```ts
isRefreshing === true
```

nên chúng không refresh nữa.

Thay vào đó:

```ts
failedQueue.push(...)
```

và chờ.

---

Khi refresh thành công:

```ts
processQueue(null, accessToken)
```

Toàn bộ request trong queue nhận được:

```text
access_new
```

Sau đó tự động retry.

---

# 7. Mapping với code

## Request Interceptor

Mục đích:

```ts
axiosClient.interceptors.request.use(...)
```

Tự động gắn Access Token vào mọi request.

Ví dụ:

```http
GET /users
```

sẽ trở thành:

```http
GET /users
Authorization: Bearer access_token
```

---

## Response Interceptor

Mục đích:

```ts
axiosClient.interceptors.response.use(...)
```

Bắt lỗi 401.

Nếu token hết hạn:

```ts
POST /auth/refresh-token
```

---

## isRefreshing

```ts
let isRefreshing = false;
```

Cho biết:

```text
Hiện tại có request refresh nào đang chạy hay không
```

---

## failedQueue

```ts
let failedQueue = [];
```

Lưu các request đang chờ token mới.

---

## processQueue()

Mục đích:

```ts
processQueue(...)
```

Thông báo cho tất cả request đang chờ:

```text
Refresh thành công
```

hoặc:

```text
Refresh thất bại
```

---

## _retry

```ts
originalRequest._retry = true;
```

Đánh dấu:

```text
Request này đã retry rồi
```

Tránh vòng lặp vô hạn.

Ví dụ:

```text
401
-> refresh
-> retry
-> 401
-> refresh
-> retry
...
```

Nếu không có _retry có thể lặp mãi mãi.

---

# 8. Khi nào bắt người dùng login lại?

Trường hợp:

```text
Refresh Token hết hạn
```

hoặc:

```text
Refresh Token bị thu hồi
```

Server trả:

```http
401
```

cho API refresh-token.

Frontend:

```ts
localStorage.removeItem(...)
```

và:

```ts
window.location.href = "/auth/login";
```

Người dùng phải đăng nhập lại.

---

# 9. Kiến thức cần biết trước khi đọc đoạn code này

Bắt buộc:

1. HTTP Request / Response
2. HTTP Status Code (200, 401, 403, 500)
3. Promise
4. async / await
5. JavaScript Closure
6. Axios
7. JWT
8. Access Token vs Refresh Token

Nên biết thêm:

1. Event Loop
2. Call Stack
3. Microtask Queue
4. Race Condition
5. Authentication Flow
6. Authorization Flow
7. Interceptor Pattern
8. Retry Pattern

---

# 10. Cấp độ kiến thức

Nếu đánh giá theo Backend/Frontend thực tế:

```text
JWT cơ bản                 : Junior
Axios Interceptor          : Junior
Refresh Token Flow         : Junior+
Refresh Queue Pattern      : Mid-level
Auth Architecture Design   : Senior
```

Đa số Fresher có thể hiểu được interceptor và refresh token sau vài ngày học.

Phần khó nhất thường là:

* Promise
* Queue
* Concurrency
* Tại sao chỉ refresh 1 lần
* Tại sao các request khác phải chờ
