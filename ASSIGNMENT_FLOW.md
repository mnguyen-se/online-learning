# HƯỚNG DẪN LUỒNG ASSIGNMENT (QUIZ)

## Tổng quan

Hệ thống hỗ trợ **Assignment dạng Quiz** với quy trình chấm điểm thủ công bởi giáo viên.

## Luồng hoạt động

### 1. Tạo Assignment

**Endpoint:** `POST /api/v1/assignments/courses/assignments`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

**Request Body:**
```json
{
  "courseId": 1,
  "title": "Kiểm tra n1",
  "description": "Bài kiểm tra đầu tiên",
  "maxScore": 20,
  "dueDate": "2026-02-20T23:59:59"
}
```


```

---

### 2. Tạo Câu Hỏi

Có 2 cách tạo câu hỏi:

#### Cách 1: Upload Excel

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/questions/upload-excel`

**Quyền:** `COURSE_MANAGER`

**Request:**
- Method: `POST`
- Content-Type: `multipart/form-data`
- Body: File Excel (.xls hoặc .xlsx)
- Tối đa 30 câu hỏi

**Format Excel:**
| Question Text | Option A | Option B | Option C | Option D | Correct Answer | Order Index | Points |
|--------------|----------|----------|----------|----------|----------------|-------------|--------|
| 「車」は gì? | xe | tàu | máy bay | xe đạp | A | 1 | 1 |





### 3. Học Sinh Làm Quiz

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/submit-quiz`

**Quyền:** `STUDENT` hoặc `ADMIN`

**Request Body:**
```json
{
  "answers": [
    {
      "questionId": 1,
      "answer": "A"
    },
    {
      "questionId": 2,
      "answer": "B"
    }
  ]
}
```

**Response:**
```json
{
  "submissionId": 5,
  "assignmentId": 1,
  "score": null,
  "maxScore": 20,
  "percentage": null,
  "details": null,
  "feedbacks": null
}
```

**Lưu ý:**
- Học sinh chỉ có thể nộp bài **1 lần** cho mỗi assignment
- Sau khi nộp, `status = SUBMITTED` (chờ giáo viên chấm)
- `score`, `details`, `feedbacks` = `null` cho đến khi giáo viên chấm

**Validation:**
- `answers` không được để trống
- `questionId` phải tồn tại trong assignment
- `answer` phải là A, B, C, hoặc D

---

### 4. Giáo Viên Xem Danh Sách Bài Nộp

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/submissions`

**Quyền:** `TEACHER` 

**Response:**
```json
[
  {
    "submissionId": 5,
    "assignmentId": 1,
    "assignmentTitle": "Kiểm tra n1",
    "studentId": 2,
    "studentName": "Nguyễn Văn A",
    "studentEmail": "student@gmail.com",
    "score": null,
    "maxScore": 20,
    "status": "SUBMITTED",
    "submittedAt": "2026-02-12T16:23:20"
  }
]
```

---

### 5. Giáo Viên Xem Chi Tiết Bài Làm

**Endpoint:** `GET /api/v1/assignments/submissions/{submissionId}`

**Quyền:** `TEACHER` hoặc `ADMIN`

**Response:**
```json
{
  "submissionId": 5,
  "assignmentId": 1,
  "assignmentTitle": "Kiểm tra n1",
  "studentId": 2,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@gmail.com",
  "studentAnswers": [
    {
      "questionId": 1,
      "questionText": "「車」は gì?",
      "optionA": "xe",
      "optionB": "tàu",
      "optionC": "máy bay",
      "optionD": "xe đạp",
      "studentAnswer": "A",
      "correctAnswer": "A",
      "isCorrect": null,
      "points": 1,
      "pointsEarned": null
    }
  ],
  "score": null,
  "maxScore": 20,
  "status": "SUBMITTED",
  "submittedAt": "2026-02-12T16:23:20",
  "feedbacks": []
}
```

**Lưu ý:**
- Khi `status = SUBMITTED`: `isCorrect` và `pointsEarned` = `null` (chưa chấm)
- Giáo viên có thể xem tất cả câu hỏi và đáp án học sinh đã chọn

---

### 6. Giáo Viên Chấm Bài

**Endpoint:** `POST /api/v1/assignments/submissions/{submissionId}/grade`

**Quyền:** `TEACHER` 

**Request Body:**
```json
{
  "score": 18,
  "comment": "Bạn làm tốt! Cần cải thiện phần từ vựng.",
  "gradedContent": null
}
```

**Hoặc giáo viên có thể tự nhập gradedContent:**
```json
{
  "score": 18,
  "comment": "Bạn làm tốt!",
  "gradedContent": "=== KẾT QUẢ BÀI LÀM ===\n\nCâu 1: Đúng\nCâu 2: Sai..."
}
```

**Response:**
```
"Grade submission successfully!"
```

**Cách hoạt động:**

1. **Tự động tính điểm:**
   - Hệ thống tự động so sánh đáp án học sinh với đáp án đúng
   - Tính `isCorrect` (true/false) cho từng câu
   - Tính `pointsEarned` (điểm đạt được) cho từng câu
   - Tổng điểm = tổng `pointsEarned` của tất cả câu

2. **Giáo viên có thể override điểm:**
   - Nếu `score` được cung cấp → dùng điểm đó
   - Nếu `score = null` → dùng điểm tự động tính

3. **Tự động tạo gradedContent:**
   - Nếu `gradedContent = null` hoặc rỗng → hệ thống tự động tạo từ bài làm của học sinh
   - Format bao gồm:
     - Tất cả câu hỏi và các lựa chọn
     - Đáp án học sinh chọn (và nội dung của lựa chọn đó)
     - Đáp án đúng (và nội dung của lựa chọn đó)
     - Điểm từng câu và tổng điểm

4. **Cập nhật status:**
   - `status = GRADED` 

5. **Tạo Feedback:**
   - Lưu `comment` và `gradedContent` vào Feedback
   - Học sinh có thể xem feedback khi xem kết quả

---

### 7. Học Sinh Xem Kết Quả

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/quiz-result`

**Quyền:** `STUDENT` hoặc `ADMIN`

**Response:**
```json
{
  "submissionId": 5,
  "assignmentId": 1,
  "score": 18,
  "maxScore": 20,
  "percentage": 90.0,
  "details": [
    {
      "questionId": 1,
      "questionText": "「車」は gì?",
      "optionA": "xe",
      "optionB": "tàu",
      "optionC": "máy bay",
      "optionD": "xe đạp",
      "studentAnswer": "A",
      "correctAnswer": "A",
      "isCorrect": true,
      "points": 1,
      "pointsEarned": 1
    },

  ],
  "feedbacks": [
    {
      "feedbackId": 1,
      "courseId": 1,
      "courseTitle": "Tiếng Nhật cơ bản",
      "studentId": 2,
      "studentName": "Nguyễn Văn A",
      "teacherId": 1,
      "teacherName": "Giáo viên B",
      "comment": "Bạn làm tốt! Cần cải thiện phần từ vựng.",
      "gradedContent": "=== KẾT QUẢ BÀI LÀM CỦA BẠN ===\n\nCâu 1: 「車」は gì?\n  A. xe\n  B. tàu\n  C. máy bay\n  D. xe đạp\n  → Bạn chọn: A (xe) - ✓ ĐÚNG\n  → Đáp án đúng: A (xe)\n  → Điểm: 1/1\n\n...",
      "createdAt": "2026-02-12T16:30:00"
    }
  ]
}
```

**Lưu ý:**

1. **Chỉ xem được khi đã chấm:**
   - Nếu `status = SUBMITTED` → Lỗi: "Bài làm đang chờ giáo viên chấm. Vui lòng chờ thông báo."
   - Chỉ xem được khi `status = GRADED` hoặc `COMPLETED`

2. **Thông tin trong response:**
   - `score`: Tổng điểm đạt được
   - `maxScore`: Tổng điểm tối đa
   - `percentage`: Tỷ lệ phần trăm
   - `details`: Chi tiết từng câu hỏi với đáp án học sinh chọn, đáp án đúng, và điểm số
   - `feedbacks`: Danh sách feedback từ giáo viên, bao gồm:
     - `comment`: Nhận xét của giáo viên
     - `gradedContent`: Bài làm đã được format (tự động tạo hoặc giáo viên nhập)

---

## Trạng thái Submission (SubmissionStatus)

- **SUBMITTED**: Học sinh đã nộp bài, chờ giáo viên chấm
- **GRADED**: Giáo viên đã chấm xong, học sinh có thể xem kết quả
- **COMPLETED**: Hoàn thành (có thể dùng cho các trường hợp đặc biệt)

**Lưu ý:** Không có trạng thái `NEEDS_REVISION`. Học sinh chỉ nộp 1 lần và nhận kết quả cuối cùng.

---

## Tóm tắt Endpoints

| Endpoint | Method | Quyền | Mô tả |
|----------|--------|-------|-------|
| `/api/v1/assignments/courses/assignments` | POST | COURSE_MANAGER, ADMIN | Tạo assignment |
| `/api/v1/assignments/{assignmentId}/questions/upload-excel` | POST | COURSE_MANAGER | Upload câu hỏi từ Excel |
| `/api/v1/assignments/{assignmentId}/questions` | GET | Tất cả | Xem danh sách câu hỏi |
| `/api/v1/assignments/{assignmentId}/submit-quiz` | POST | STUDENT, ADMIN | Học sinh nộp bài quiz |
| `/api/v1/assignments/{assignmentId}/submissions` | GET | TEACHER, ADMIN | Giáo viên xem danh sách bài nộp |
| `/api/v1/assignments/submissions/{submissionId}` | GET | TEACHER, ADMIN | Giáo viên xem chi tiết bài làm |
| `/api/v1/assignments/submissions/{submissionId}/grade` | POST | TEACHER, ADMIN | Giáo viên chấm bài |
| `/api/v1/assignments/{assignmentId}/quiz-result` | GET | STUDENT, ADMIN | Học sinh xem kết quả |

---

## Ví dụ Luồng Hoàn Chỉnh

1. **COURSE_MANAGER** tạo assignment: `POST /api/v1/assignments/courses/assignments`
2. **COURSE_MANAGER** upload câu hỏi: `POST /api/v1/assignments/1/questions/upload-excel`
3. **STUDENT** xem câu hỏi: `GET /api/v1/assignments/1/questions`
4. **STUDENT** làm và nộp quiz: `POST /api/v1/assignments/1/submit-quiz`
5. **TEACHER** xem danh sách bài nộp: `GET /api/v1/assignments/1/submissions`
6. **TEACHER** xem chi tiết bài làm: `GET /api/v1/assignments/submissions/5`
7. **TEACHER** chấm bài: `POST /api/v1/assignments/submissions/5/grade`
8. **STUDENT** xem kết quả: `GET /api/v1/assignments/1/quiz-result`

---

## Lưu ý Quan Trọng

1. **Học sinh chỉ nộp 1 lần:** Không thể nộp lại sau khi đã nộp
2. **Chấm điểm thủ công:** Giáo viên phải chấm từng bài, hệ thống chỉ tự động tính điểm dựa trên đáp án đúng/sai
3. **Tự động tạo gradedContent:** Nếu giáo viên không nhập, hệ thống tự động tạo từ bài làm của học sinh
4. **Không có yêu cầu làm lại:** Học sinh nhận kết quả cuối cùng, không có chức năng yêu cầu làm lại
