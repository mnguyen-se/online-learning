# Hướng dẫn Migration Database

## Vấn đề
Cột `correct_answer` trong bảng `question` có kích thước quá nhỏ (VARCHAR) để lưu đáp án mẫu dài cho Writing Assignment (ví dụ: "わたしは学生です", hoặc các đoạn văn dài).

## Giải pháp

### Cách 1: Chạy SQL trực tiếp (Khuyến nghị - Nhanh nhất)

Chạy SQL sau trên MySQL database:

```sql
ALTER TABLE question MODIFY COLUMN correct_answer TEXT NULL;
```

**Lưu ý:** Nếu cột `correct_answer` đang là NOT NULL, có thể cần chạy:

```sql
ALTER TABLE question MODIFY COLUMN correct_answer TEXT NULL;
```

### Cách 2: Sử dụng file migration

File migration đã được tạo tại: `src/main/resources/migration_fix_correct_answer.sql`

Bạn có thể:
1. Copy nội dung file SQL
2. Chạy trực tiếp trên MySQL client hoặc phpMyAdmin

### Cách 3: Sử dụng JPA/Hibernate auto-update

Nếu `ddl-auto: update` đang bật, có thể cần:
1. Xóa bảng `question` (nếu dữ liệu test không quan trọng)
2. Restart application để Hibernate tạo lại schema

**⚠️ CẢNH BÁO:** Cách này sẽ mất dữ liệu, chỉ dùng cho môi trường development!

## Kiểm tra sau khi migration

Sau khi chạy migration, kiểm tra schema:

```sql
DESCRIBE question;
```

Cột `correct_answer` phải có kiểu `text` hoặc `longtext`.

## Test

Sau khi migration, thử tạo lại câu hỏi REORDER:

```json
{
  "questionText": "Sắp xếp các từ sau thành câu đúng:",
  "questionType": "REORDER",
  "orderIndex": 4,
  "points": 3,
  "items": ["は", "学生", "です", "わたし"],
  "sampleAnswer": "わたしは学生です"
}
```

Nếu thành công, không còn lỗi "Data too long for column 'correct_answer'".
