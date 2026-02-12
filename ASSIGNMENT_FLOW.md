# HƯỚNG DẪN LUỒNG ASSIGNMENT

## Tổng quan

Hệ thống hỗ trợ **2 loại Assignment**:
1. **Assignment dạng Quiz**: Tự động chấm điểm, học sinh nhận kết quả ngay lập tức
2. **Assignment dạng Writing**: Điền vào chỗ trống, giáo viên chấm thủ công

---

# PHẦN 1: ASSIGNMENT DẠNG QUIZ

## Tổng quan

Hệ thống hỗ trợ **Assignment dạng Quiz** với quy trình **tự động chấm điểm**. Học sinh nộp bài và nhận kết quả ngay lập tức, có thể nộp lại để cải thiện điểm số.

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
  "dueDate": "2026-02-20T23:59:59",
  "assignmentType": "QUIZ"
}
```

**Lưu ý:** 
- `assignmentType` có thể là `"QUIZ"` hoặc `"WRITING"`
- Nếu không chỉ định, mặc định là `"QUIZ"`

**Response:**
```json
{
  "assignmentId": 1,
  "courseId": 1,
  "title": "Kiểm tra n1",
  "description": "Bài kiểm tra đầu tiên",
  "maxScore": 20,
  "dueDate": "2026-02-20T23:59:59",
  "createdAt": "2026-02-12T10:00:00"
}
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
2: Xem danh sách câu hỏi

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/questions`

**Quyền:** Tất cả

**Response:**
```json
[
  {
    "questionId": 1,
    "assignmentId": 1,
    "questionText": "「車」は gì?",
    "optionA": "xe",
    "optionB": "tàu",
    "optionC": "máy bay",
    "optionD": "xe đạp",
    "correctAnswer": "A",
    "orderIndex": 1,
    "points": 1
  }
]
```

---

### 3. Học Sinh Làm Quiz

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/submit-quiz`

**Quyền:** `STUDENT` 

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

**Response (Kết quả ngay lập tức):**
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
    {
      "questionId": 2,
      "questionText": "「高い」は gì?",
      "optionA": "thấp",
      "optionB": "cao / đắt",
      "optionC": "rẻ",
      "optionD": "dài",
      "studentAnswer": "C",
      "correctAnswer": "B",
      "isCorrect": false,
      "points": 1,
      "pointsEarned": 0
    }
  ],
  "feedbacks": []
}
```

**Lưu ý:**
- Học sinh **có thể nộp lại** để cải thiện điểm số
- Khi nộp lại, hệ thống sẽ **tự động xóa** bài làm cũ và lưu bài làm mới
- Kết quả được **tự động chấm** và trả về ngay lập tức
- `status = GRADED` ngay sau khi nộp

**Validation:**
- `answers` không được để trống
- `questionId` phải tồn tại trong assignment
- `answer` phải là A, B, C, hoặc D

**Cách hoạt động tự động chấm:**
1. Hệ thống so sánh đáp án học sinh với đáp án đúng
2. Tính `isCorrect` (true/false) cho từng câu
3. Tính `pointsEarned` (điểm đạt được) cho từng câu
4. Tổng điểm = tổng `pointsEarned` của tất cả câu
5. Tính `percentage` (tỷ lệ phần trăm)
6. Trả về kết quả đầy đủ ngay lập tức

---

### 4. Học Sinh Xem Kết Quả

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
    {
      "questionId": 2,
      "questionText": "「高い」は gì?",
      "optionA": "thấp",
      "optionB": "cao / đắt",
      "optionC": "rẻ",
      "optionD": "dài",
      "studentAnswer": "C",
      "correctAnswer": "B",
      "isCorrect": false,
      "points": 1,
      "pointsEarned": 0
    }
  ],
  "feedbacks": []
}
```

**Lưu ý:**
- Chỉ xem được khi đã nộp bài
- Nếu chưa nộp → Lỗi: "Bạn chưa nộp bài cho assignment này"
- Response bao gồm:
  - `score`: Tổng điểm đạt được
  - `maxScore`: Tổng điểm tối đa
  - `percentage`: Tỷ lệ phần trăm
  - `details`: Chi tiết từng câu hỏi với đáp án học sinh chọn, đáp án đúng, và điểm số

---

## Trạng thái Submission (SubmissionStatus)

- **GRADED**: Học sinh đã nộp bài và được chấm tự động (luôn luôn sau khi nộp)

**Lưu ý:** Không có trạng thái `SUBMITTED` hoặc `NEEDS_REVISION`. Hệ thống tự động chấm ngay sau khi nộp.

---

## Tóm tắt Endpoints

| Endpoint | Method | Quyền | Mô tả |
|----------|--------|-------|-------|
| `/api/v1/assignments/courses/assignments` | POST | COURSE_MANAGER, ADMIN | Tạo assignment |
| `/api/v1/assignments/{assignmentId}/questions/upload-excel` | POST | COURSE_MANAGER | Upload câu hỏi từ Excel |
| `/api/v1/assignments/{assignmentId}/questions` | GET | Tất cả | Xem danh sách câu hỏi |
| `/api/v1/assignments/{assignmentId}/submit-quiz` | POST | STUDENT, ADMIN | Học sinh nộp bài quiz và nhận kết quả ngay |
| `/api/v1/assignments/{assignmentId}/quiz-result` | GET | STUDENT, ADMIN | Học sinh xem kết quả quiz |
| `/api/v1/assignments/get/{assignmentId}` | GET | Tất cả | Xem thông tin assignment |
| `/api/v1/assignments/course/{courseId}` | GET | Tất cả | Lấy danh sách assignment theo course |

---

## Ví dụ Luồng Hoàn Chỉnh

1. **COURSE_MANAGER** tạo assignment: `POST /api/v1/assignments/courses/assignments`
2. **COURSE_MANAGER** upload câu hỏi: `POST /api/v1/assignments/1/questions/upload-excel`
3. **STUDENT** xem câu hỏi: `GET /api/v1/assignments/1/questions`
4. **STUDENT** làm và nộp quiz: `POST /api/v1/assignments/1/submit-quiz`
   - Nhận kết quả ngay lập tức với điểm số và chi tiết từng câu
5. **STUDENT** có thể nộp lại để cải thiện điểm: `POST /api/v1/assignments/1/submit-quiz` (lần 2, 3, ...)
6. **STUDENT** xem kết quả bất cứ lúc nào: `GET /api/v1/assignments/1/quiz-result`

---

## Lưu ý Quan Trọng

1. **Tự động chấm điểm:** Hệ thống tự động so sánh đáp án và tính điểm ngay khi học sinh nộp bài
2. **Nhận kết quả ngay:** Học sinh nhận kết quả đầy đủ (điểm số, chi tiết từng câu) ngay sau khi nộp
3. **Có thể nộp lại:** Học sinh có thể nộp lại nhiều lần để cải thiện điểm số, bài làm cũ sẽ bị ghi đè
4. **Không cần giáo viên:** Hệ thống hoàn toàn tự động, không cần giáo viên chấm bài
5. **Không có feedback:** Vì tự động chấm nên không có feedback từ giáo viên

---

## So sánh với Luồng Cũ

| Tính năng | Luồng Cũ | Luồng Mới |
|-----------|----------|-----------|
| Chấm điểm | Giáo viên chấm thủ công | Tự động chấm |
| Nhận kết quả | Phải chờ giáo viên chấm | Nhận ngay lập tức |
| Nộp lại | Không cho phép (hoặc chỉ khi NEEDS_REVISION) | Cho phép nộp lại nhiều lần |
| Feedback | Có feedback từ giáo viên | Không có feedback |
| Status | SUBMITTED → GRADED | GRADED (ngay sau khi nộp) |

---

# PHẦN 2: ASSIGNMENT DẠNG WRITING (ĐIỀN VÀO CHỖ TRỐNG)

## Tổng quan

Hệ thống hỗ trợ **Assignment dạng Writing** với các bài tập **điền vào chỗ trống**. Học sinh nộp bài và giáo viên sẽ **chấm điểm thủ công**. Ví dụ:

- わたし（　）学生です。
- 日本（　）行きます。
- パン（　）食べます。

## Luồng hoạt động

### 1. Tạo Assignment

**Endpoint:** `POST /api/v1/assignments/courses/assignments`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

**Request Body:**
```json
{
  "courseId": 1,
  "title": "Bài tập điền trợ từ",
  "description": "Điền trợ từ thích hợp vào chỗ trống",
  "maxScore": 20,
  "dueDate": "2026-02-20T23:59:59",
  "assignmentType": "WRITING"
}
```

**Response:**
```json
{
  "assignmentId": 2,
  "courseId": 1,
  "title": "Bài tập điền trợ từ",
  "description": "Điền trợ từ thích hợp vào chỗ trống",
  "maxScore": 20,
  "dueDate": "2026-02-20T23:59:59",
  "assignmentType": "WRITING",
  "createdAt": "2026-02-12T10:00:00"
}
```

---

### 2. Tạo Câu Hỏi Điền Vào Chỗ Trống

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/writing-questions`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

**Request Body:**
```json
{
  "questionText": "わたし（　）学生です。",
  "orderIndex": 1,
  "points": 2,
  "sampleAnswer": "は"
}
```

**Response:**
```json
{
  "questionId": 1,
  "assignmentId": 2,
  "questionText": "わたし（　）学生です。",
  "optionA": null,
  "optionB": null,
  "optionC": null,
  "optionD": null,
  "correctAnswer": "は",
  "orderIndex": 1,
  "points": 2
}
```

**Lưu ý:**
- `questionText`: Câu hỏi với chỗ trống (　) để học sinh điền
- `sampleAnswer`: Đáp án mẫu (optional) để giáo viên tham khảo khi chấm
- `points`: Điểm cho câu hỏi này

**Ví dụ tạo nhiều câu hỏi:**
```json
[
  {
    "questionText": "わたし（　）学生です。",
    "orderIndex": 1,
    "points": 2,
    "sampleAnswer": "は"
  },
  {
    "questionText": "日本（　）行きます。",
    "orderIndex": 2,
    "points": 2,
    "sampleAnswer": "へ"
  },
  {
    "questionText": "パン（　）食べます。",
    "orderIndex": 3,
    "points": 2,
    "sampleAnswer": "を"
  }
]
```

---

### 3. Học Sinh Xem Câu Hỏi

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/questions`

**Quyền:** Tất cả

**Response:**
```json
[
  {
    "questionId": 1,
    "assignmentId": 2,
    "questionText": "わたし（　）学生です。",
    "optionA": null,
    "optionB": null,
    "optionC": null,
    "optionD": null,
    "correctAnswer": "は",
    "orderIndex": 1,
    "points": 2
  },
  {
    "questionId": 2,
    "assignmentId": 2,
    "questionText": "日本（　）行きます。",
    "optionA": null,
    "optionB": null,
    "optionC": null,
    "optionD": null,
    "correctAnswer": "へ",
    "orderIndex": 2,
    "points": 2
  }
]
```

---

### 4. Học Sinh Nộp Bài

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/submit-writing`

**Quyền:** `STUDENT` hoặc `ADMIN`

**Request Body:**
```json
{
  "answers": [
    {
      "questionId": 1,
      "answer": "は"
    },
    {
      "questionId": 2,
      "answer": "へ"
    },
    {
      "questionId": 3,
      "answer": "を"
    }
  ]
}
```

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 2,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": null,
  "maxScore": 20,
  "status": "SUBMITTED",
  "feedback": null,
  "answers": [
    {
      "answerId": 1,
      "questionId": 1,
      "questionText": "わたし（　）学生です。",
      "studentAnswer": "は",
      "points": 2,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "は"
    },
    {
      "answerId": 2,
      "questionId": 2,
      "questionText": "日本（　）行きます。",
      "studentAnswer": "へ",
      "points": 2,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "へ"
    }
  ]
}
```

**Lưu ý:**
- Sau khi nộp, `status = SUBMITTED` (chờ giáo viên chấm)
- `score`, `pointsEarned`, `isCorrect` đều là `null` cho đến khi giáo viên chấm
- Học sinh có thể nộp lại (ghi đè bài cũ) trước khi giáo viên chấm

---

### 5. Giáo Viên Xem Danh Sách Submissions

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/writing-submissions`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

**Response:**
```json
[
  {
    "submissionId": 1,
    "assignmentId": 2,
    "studentId": 5,
    "studentName": "Nguyễn Văn A",
    "studentEmail": "student@example.com",
    "submittedAt": "2026-02-15T10:30:00",
    "score": null,
    "maxScore": 20,
    "status": "SUBMITTED",
    "feedback": null,
    "answers": [...]
  },
  {
    "submissionId": 2,
    "assignmentId": 2,
    "studentId": 6,
    "studentName": "Trần Thị B",
    "studentEmail": "student2@example.com",
    "submittedAt": "2026-02-15T11:00:00",
    "score": null,
    "maxScore": 20,
    "status": "SUBMITTED",
    "feedback": null,
    "answers": [...]
  }
]
```

---

### 6. Giáo Viên Xem Chi Tiết Submission

**Endpoint:** `GET /api/v1/assignments/writing-submissions/{submissionId}`

**Quyền:** 
- `STUDENT`: Chỉ xem được submission của mình
- `COURSE_MANAGER` / `ADMIN`: Xem được tất cả

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 2,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": null,
  "maxScore": 20,
  "status": "SUBMITTED",
  "feedback": null,
  "answers": [
    {
      "answerId": 1,
      "questionId": 1,
      "questionText": "わたし（　）学生です。",
      "studentAnswer": "は",
      "points": 2,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "は"
    },
    {
      "answerId": 2,
      "questionId": 2,
      "questionText": "日本（　）行きます。",
      "studentAnswer": "へ",
      "points": 2,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "へ"
    }
  ]
}
```

---

### 7. Giáo Viên Chấm Điểm

**Endpoint:** `POST /api/v1/assignments/writing-submissions/{submissionId}/grade`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

**Request Body:**
```json
{
  "score": 18,
  "feedback": "Làm tốt! Cần chú ý thêm về cách sử dụng trợ từ.",
  "answerGrades": [
    {
      "answerId": 1,
      "pointsEarned": 2,
      "isCorrect": true
    },
    {
      "answerId": 2,
      "pointsEarned": 2,
      "isCorrect": true
    },
    {
      "answerId": 3,
      "pointsEarned": 1,
      "isCorrect": false
    }
  ]
}
```

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 2,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": 18,
  "maxScore": 20,
  "status": "GRADED",
  "feedback": "Làm tốt! Cần chú ý thêm về cách sử dụng trợ từ.",
  "answers": [
    {
      "answerId": 1,
      "questionId": 1,
      "questionText": "わたし（　）学生です。",
      "studentAnswer": "は",
      "points": 2,
      "pointsEarned": 2,
      "isCorrect": true,
      "sampleAnswer": "は"
    },
    {
      "answerId": 2,
      "questionId": 2,
      "questionText": "日本（　）行きます。",
      "studentAnswer": "へ",
      "points": 2,
      "pointsEarned": 2,
      "isCorrect": true,
      "sampleAnswer": "へ"
    },
    {
      "answerId": 3,
      "questionId": 3,
      "questionText": "パン（　）食べます。",
      "studentAnswer": "を",
      "points": 2,
      "pointsEarned": 1,
      "isCorrect": false,
      "sampleAnswer": "を"
    }
  ]
}
```

**Lưu ý:**
- Sau khi chấm, `status = GRADED`
- `score`: Tổng điểm giáo viên chấm (có thể khác với tổng `pointsEarned` nếu giáo viên muốn điều chỉnh)
- `pointsEarned`: Điểm đạt được cho từng câu
- `isCorrect`: Đúng/sai cho từng câu
- `feedback`: Nhận xét từ giáo viên (optional)
- Giáo viên có thể chấm lại để cập nhật điểm

---

## Trạng thái Submission (SubmissionStatus) cho Writing Assignment

- **SUBMITTED**: Học sinh đã nộp bài, đang chờ giáo viên chấm
- **GRADED**: Giáo viên đã chấm điểm
- **NEEDS_REVISION**: (Có thể dùng trong tương lai) Cần học sinh làm lại

---

## Tóm tắt Endpoints cho Writing Assignment

| Endpoint | Method | Quyền | Mô tả |
|----------|--------|-------|-------|
| `/api/v1/assignments/courses/assignments` | POST | COURSE_MANAGER, ADMIN | Tạo assignment (với `assignmentType: "WRITING"`) |
| `/api/v1/assignments/{assignmentId}/writing-questions` | POST | COURSE_MANAGER, ADMIN | Tạo câu hỏi điền vào chỗ trống |
| `/api/v1/assignments/{assignmentId}/questions` | GET | Tất cả | Xem danh sách câu hỏi |
| `/api/v1/assignments/{assignmentId}/submit-writing` | POST | STUDENT, ADMIN | Học sinh nộp bài |
| `/api/v1/assignments/{assignmentId}/writing-submissions` | GET | COURSE_MANAGER, ADMIN | Giáo viên xem danh sách submissions |
| `/api/v1/assignments/writing-submissions/{submissionId}` | GET | Tất cả | Xem chi tiết submission |
| `/api/v1/assignments/writing-submissions/{submissionId}/grade` | POST | COURSE_MANAGER, ADMIN | Giáo viên chấm điểm |

---

## Ví dụ Luồng Hoàn Chỉnh cho Writing Assignment

1. **COURSE_MANAGER** tạo assignment: `POST /api/v1/assignments/courses/assignments` (với `assignmentType: "WRITING"`)
2. **COURSE_MANAGER** tạo câu hỏi: `POST /api/v1/assignments/2/writing-questions` (tạo từng câu hoặc nhiều câu)
3. **STUDENT** xem câu hỏi: `GET /api/v1/assignments/2/questions`
4. **STUDENT** làm và nộp bài: `POST /api/v1/assignments/2/submit-writing`
   - Status = `SUBMITTED`, chờ giáo viên chấm
5. **COURSE_MANAGER** xem danh sách submissions: `GET /api/v1/assignments/2/writing-submissions`
6. **COURSE_MANAGER** xem chi tiết và chấm điểm: 
   - `GET /api/v1/assignments/writing-submissions/1`
   - `POST /api/v1/assignments/writing-submissions/1/grade`
7. **STUDENT** xem kết quả: `GET /api/v1/assignments/writing-submissions/1`
   - Status = `GRADED`, có điểm và feedback

---

## So sánh QUIZ và WRITING Assignment

| Tính năng | QUIZ | WRITING |
|-----------|------|---------|
| Loại câu hỏi | Multiple choice (A, B, C, D) | Điền vào chỗ trống (text) |
| Chấm điểm | Tự động | Thủ công bởi giáo viên |
| Nhận kết quả | Ngay lập tức | Sau khi giáo viên chấm |
| Nộp lại | Có thể nộp lại nhiều lần | Có thể nộp lại trước khi giáo viên chấm |
| Feedback | Không có | Có feedback từ giáo viên |
| Status sau khi nộp | GRADED (ngay) | SUBMITTED → GRADED |
| Điểm từng câu | Tự động tính | Giáo viên chấm thủ công |
