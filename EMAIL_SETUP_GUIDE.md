# Hướng dẫn cấu hình Email Service

## Tổng quan

Hệ thống tự động gửi email cho học sinh khi giáo viên chấm điểm Writing Assignment xong.

## Quy trình hoạt động

1. Học sinh nộp bài Writing Assignment
2. Giáo viên chấm điểm (POST /api/v1/assignments/writing-submissions/{submissionId}/grade)
3. Hệ thống tự động gửi email thông báo kết quả đến email của học sinh
4. Học sinh nhận email với thông tin: điểm số, feedback, tên bài tập

## Cấu hình Email

### Bước 1: Tạo App Password cho Gmail

1. Đăng nhập vào Google Account
2. Vào **Security** → **2-Step Verification** (bật nếu chưa bật)
3. Vào **Security** → **App passwords**
4. Chọn **Mail** và **Other (Custom name)**
5. Nhập tên: "Online Learning System"
6. Copy **App Password** (16 ký tự, không có khoảng trắng)

### Bước 2: Cấu hình Environment Variables

Tạo file `.env` trong thư mục root của project (hoặc set environment variables):

```env
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password-16-chars
```

**Lưu ý:**
- `EMAIL_USERNAME`: Email Gmail của bạn (ví dụ: `your-email@gmail.com`)
- `EMAIL_PASSWORD`: App Password (16 ký tự, không phải mật khẩu Gmail thường)

### Bước 3: Cấu hình application.yaml

File `application.yaml` đã được cấu hình sẵn:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### Bước 4: Test Email Service

1. Chạy application
2. Giáo viên chấm điểm một Writing Assignment
3. Kiểm tra email của học sinh xem có nhận được email không

## Nội dung Email

Email sẽ chứa:
- Tên học sinh
- Tên bài tập
- Điểm số (score/maxScore)
- Feedback từ giáo viên (nếu có)
- Link để xem chi tiết (có thể thêm sau)

## Xử lý lỗi

- Nếu gửi email thất bại, hệ thống sẽ log lỗi nhưng **KHÔNG** throw exception
- Việc chấm điểm vẫn thành công dù email không gửi được
- Kiểm tra log để xem lỗi cụ thể

## Troubleshooting

### Lỗi: "Authentication failed"

**Nguyên nhân:** 
- App Password sai
- Chưa bật 2-Step Verification
- Email username sai

**Giải pháp:**
1. Kiểm tra lại App Password
2. Đảm bảo đã bật 2-Step Verification
3. Kiểm tra EMAIL_USERNAME trong environment variables

### Lỗi: "Connection timeout"

**Nguyên nhân:**
- Firewall chặn port 587
- Network không ổn định

**Giải pháp:**
1. Kiểm tra firewall
2. Thử dùng port 465 với SSL (cần sửa config)

### Email không gửi được nhưng không có lỗi

**Nguyên nhân:**
- Email service không được inject đúng
- Environment variables chưa được load

**Giải pháp:**
1. Kiểm tra log khi start application
2. Đảm bảo `.env` file được load (nếu dùng dotenv)
3. Kiểm tra EmailService có được @Autowired đúng không

## Sử dụng Email Provider khác

### Outlook/Hotmail

```yaml
spring:
  mail:
    host: smtp-mail.outlook.com
    port: 587
```

### Yahoo

```yaml
spring:
  mail:
    host: smtp.mail.yahoo.com
    port: 587
```

### Custom SMTP Server

```yaml
spring:
  mail:
    host: your-smtp-server.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
```

## Lưu ý bảo mật

1. **KHÔNG** commit file `.env` lên Git
2. **KHÔNG** hardcode email/password trong code
3. Sử dụng environment variables hoặc secrets management
4. App Password chỉ dùng cho ứng dụng, không dùng cho login thường

## Cải thiện trong tương lai

- [ ] Gửi email HTML với template đẹp hơn
- [ ] Gửi email bất đồng bộ (async) để không làm chậm response
- [ ] Thêm link xem chi tiết kết quả trong email
- [ ] Thêm attachment (nếu cần)
- [ ] Queue email để retry nếu gửi thất bại
