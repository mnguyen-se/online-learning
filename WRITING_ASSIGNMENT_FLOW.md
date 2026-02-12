# HƯỚNG DẪN LUỒNG WRITING ASSIGNMENT

## Tổng quan

Hệ thống hỗ trợ **Assignment dạng Writing** với **4 loại câu hỏi**:
1. **FILL_BLANK**: Điền vào chỗ trống - **Học sinh tự viết/gõ đáp án** (ví dụ: わたし（　）学生です。)
2. **REORDER**: Sắp xếp câu/từ thành câu đúng - **Học sinh kéo thả/sắp xếp** các từ có sẵn
3. **MATCHING**: Nối cột A với cột B - **Học sinh chọn và nối** các cặp từ có sẵn
4. **ESSAY_WRITING**: Viết bài văn - **Học sinh tự viết** một đoạn văn (có chủ đề, số từ tối thiểu/tối đa, hướng dẫn)

Tất cả các loại câu hỏi này có thể được kết hợp trong **một đề assignment duy nhất**. **Chỉ TEACHER** mới được chấm điểm thủ công cho tất cả các loại câu hỏi.

---

## Phân biệt với QUIZ Assignment

| Đặc điểm | QUIZ | WRITING |
|----------|------|---------|
| **assignmentType** | `"QUIZ"` | `"WRITING"` |
| **Loại câu hỏi** | Multiple choice (A, B, C, D) | FILL_BLANK, REORDER, MATCHING, ESSAY_WRITING |
| **Chấm điểm** | Tự động | Thủ công bởi **TEACHER** |
| **Nhận kết quả** | Ngay lập tức | Sau khi **TEACHER** chấm |
| **Status sau khi nộp** | `GRADED` (ngay) | `SUBMITTED` → `GRADED` |
| **Ai chấm điểm** | Hệ thống tự động | **Chỉ TEACHER** |

---

## Luồng hoạt động

### Bước 1: Tạo Assignment (WRITING)

**Endpoint:** `POST /api/v1/assignments/courses/assignments`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

**Request Body:**
```json
{
  "courseId": 1,
  "title": "Bài kiểm tra tiếng Nhật N5 - Tuần 3",
  "description": "Bài kiểm tra tổng hợp các kỹ năng: điền từ, sắp xếp câu, nối cột, và viết đoạn văn",
  "maxScore": 50,
  "dueDate": "2026-02-25T23:59:59",
  "assignmentType": "WRITING"
}
```

**Response:**
```json
{
  "assignmentId": 5,
  "courseId": 1,
  "title": "Bài kiểm tra tiếng Nhật N5 - Tuần 3",
  "description": "Bài kiểm tra tổng hợp các kỹ năng: điền từ, sắp xếp câu, nối cột, và viết đoạn văn",
  "maxScore": 50,
  "dueDate": "2026-02-25T23:59:59",
  "assignmentType": "WRITING",
  "createdAt": "2026-02-12T10:00:00"
}
```

**Lưu ý:** 
- Phải chỉ định `assignmentType: "WRITING"` để phân biệt với QUIZ
- Nếu không chỉ định, mặc định là `"QUIZ"`

---

### Bước 2: Tạo các câu hỏi (theo thứ tự)

Sau khi có `assignmentId`, giáo viên tạo từng câu hỏi với các loại khác nhau.

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/writing-questions`

**Quyền:** `COURSE_MANAGER` hoặc `ADMIN`

---

#### Loại 1: FILL_BLANK (Điền vào chỗ trống)

**Ví dụ câu 1:**
```json
{
  "questionText": "わたし（　）学生です。",
  "questionType": "FILL_BLANK",
  "orderIndex": 1,
  "points": 2,
  "sampleAnswer": "は"
}
```

**Ví dụ câu 2:**
```json
{
  "questionText": "日本（　）行きます。",
  "questionType": "FILL_BLANK",
  "orderIndex": 2,
  "points": 2,
  "sampleAnswer": "へ"
}
```

**Ví dụ câu 3:**
```json
{
  "questionText": "パン（　）食べます。",
  "questionType": "FILL_BLANK",
  "orderIndex": 3,
  "points": 2,
  "sampleAnswer": "を"
}
```

**Response:**
```json
{
  "questionId": 1,
  "assignmentId": 5,
  "questionText": "わたし（　）学生です。",
  "questionType": "FILL_BLANK",
  "orderIndex": 1,
  "points": 2,
  "correctAnswer": "は"
}
```

---

#### Loại 2: REORDER (Sắp xếp câu)

**Ví dụ câu 4:**
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

**Ví dụ câu 5:**
```json
{
  "questionText": "Sắp xếp các từ sau thành câu đúng:",
  "questionType": "REORDER",
  "orderIndex": 5,
  "points": 3,
  "items": ["を", "食べます", "パン", "わたし"],
  "sampleAnswer": "わたしはパンを食べます"
}
```

**Response:**
```json
{
  "questionId": 4,
  "assignmentId": 5,
  "questionText": "Sắp xếp các từ sau thành câu đúng:",
  "questionType": "REORDER",
  "orderIndex": 4,
  "points": 3,
  "items": ["は", "学生", "です", "わたし"],
  "correctAnswer": "わたしは学生です"
}
```

---

#### Loại 3: MATCHING (Nối cột A với cột B)

**Ví dụ câu 6:**
```json
{
  "questionText": "Nối cột A với cột B cho đúng:",
  "questionType": "MATCHING",
  "orderIndex": 6,
  "points": 5,
  "columnA": [
    {"id": "1", "text": "1. 車"},
    {"id": "2", "text": "2. 高い"},
    {"id": "3", "text": "3. 本"},
    {"id": "4", "text": "4. 学校"}
  ],
  "columnB": [
    {"id": "1", "text": "a. xe"},
    {"id": "2", "text": "b. cao / đắt"},
    {"id": "3", "text": "c. sách"},
    {"id": "4", "text": "d. trường học"}
  ],
  "sampleAnswer": "1-a, 2-b, 3-c, 4-d"
}
```

**Response:**
```json
{
  "questionId": 6,
  "assignmentId": 5,
  "questionText": "Nối cột A với cột B cho đúng:",
  "questionType": "MATCHING",
  "orderIndex": 6,
  "points": 5,
  "columnA": [
    {"id": "1", "text": "1. 車"},
    {"id": "2", "text": "2. 高い"},
    {"id": "3", "text": "3. 本"},
    {"id": "4", "text": "4. 学校"}
  ],
  "columnB": [
    {"id": "1", "text": "a. xe"},
    {"id": "2", "text": "b. cao / đắt"},
    {"id": "3", "text": "c. sách"},
    {"id": "4", "text": "d. trường học"}
  ],
  "correctAnswer": "1-a, 2-b, 3-c, 4-d"
}
```

---

#### Loại 4: ESSAY_WRITING (Viết bài văn)

**Ví dụ câu 7:**
```json
{
  "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
  "questionType": "ESSAY_WRITING",
  "orderIndex": 7,
  "points": 20,
  "topic": "Gia đình của tôi",
  "minWords": 100,
  "maxWords": 150,
  "instructions": "Hãy viết về các thành viên trong gia đình bạn, sở thích của họ và những hoạt động gia đình thường làm cùng nhau. Sử dụng các từ vựng và ngữ pháp đã học.",
  "sampleAnswer": "私の家族は4人です。父、母、姉と私です。父は会社員で、母は教師です。姉は大学生です。週末、私たちは一緒に料理をしたり、映画を見たりします。家族の時間が一番大切です。"
}
```

**Response:**
```json
{
  "questionId": 7,
  "assignmentId": 5,
  "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
  "questionType": "ESSAY_WRITING",
  "orderIndex": 7,
  "points": 20,
  "topic": "Gia đình của tôi",
  "minWords": 100,
  "maxWords": 150,
  "instructions": "Hãy viết về các thành viên trong gia đình bạn...",
  "correctAnswer": "私の家族は4人です。..."
}
```

---

### Bước 3: Xem đề thi đã tạo

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/questions`

**Quyền:** Tất cả

**Response:**
```json
[

  {
    "questionId": 4,
    "assignmentId": 5,
    "questionText": "Sắp xếp các từ sau thành câu đúng:",
    "questionType": "REORDER",
    "orderIndex": 4,
    "points": 3,
    "items": ["は", "学生", "です", "わたし"]
  },
  {
    "questionId": 6,
    "assignmentId": 5,
    "questionText": "Nối cột A với cột B cho đúng:",
    "questionType": "MATCHING",
    "orderIndex": 6,
    "points": 5,
    "columnA": [
      {"id": "1", "text": "1. 車"},
      {"id": "2", "text": "2. 高い"}
    ],
    "columnB": [
      {"id": "1", "text": "a. xe"},
      {"id": "2", "text": "b. cao / đắt"}
    ]
  },
  {
    "questionId": 7,
    "assignmentId": 5,
    "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
    "questionType": "ESSAY_WRITING",
    "orderIndex": 7,
    "points": 20,
    "topic": "Gia đình của tôi",
    "minWords": 100,
    "maxWords": 150,
    "instructions": "Hãy viết về các thành viên trong gia đình bạn..."
  }
]
```

---

### Bước 4: Học sinh nộp bài

**Endpoint:** `POST /api/v1/assignments/{assignmentId}/submit-writing`

**Quyền:** `STUDENT` 

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
    },
    {
      "questionId": 4,
      "orderedItems": ["わたし", "は", "学生", "です"]
    },
    {
      "questionId": 5,
      "orderedItems": ["わたし", "は", "パン", "を", "食べます"]
    },
    {
      "questionId": 6,
      "matchingPairs": [
        {"aId": "1", "bId": "1"},
        {"aId": "2", "bId": "2"},
        {"aId": "3", "bId": "3"},
        {"aId": "4", "bId": "4"}
      ]
    },
    {
      "questionId": 7,
      "answer": "私の家族は4人です。父、母、姉と私です。父は会社員で、母は教師です。姉は大学生です。週末、私たちは一緒に料理をしたり、映画を見たりします。家族の時間が一番大切です。"
    }
  ]
}
```

**Lưu ý:**
- **FILL_BLANK** và **ESSAY_WRITING**: dùng field `answer` (string)
- **REORDER**: dùng field `orderedItems` (array of strings)
- **MATCHING**: dùng field `matchingPairs` (array of objects với `aId` và `bId`)

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 5,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": null,
  "maxScore": 50,
  "status": "SUBMITTED",
  "feedback": null,
  "answers": [

    {
      "answerId": 4,
      "questionId": 4,
      "questionText": "Sắp xếp các từ sau thành câu đúng:",
      "studentAnswer": "[\"わたし\",\"は\",\"学生\",\"です\"]",
      "points": 3,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "わたしは学生です"
    },
    {
      "answerId": 6,
      "questionId": 6,
      "questionText": "Nối cột A với cột B cho đúng:",
      "studentAnswer": "[{\"aId\":\"1\",\"bId\":\"1\"},{\"aId\":\"2\",\"bId\":\"2\"}]",
      "points": 5,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "1-a, 2-b, 3-c, 4-d"
    },
    {
      "answerId": 7,
      "questionId": 7,
      "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
      "studentAnswer": "私の家族は4人です。...",
      "points": 20,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "私の家族は4人です。..."
    }
  ]
}
```

**Lưu ý:**
- `studentAnswer` cho REORDER và MATCHING được lưu dưới dạng JSON string
- Frontend cần parse JSON để hiển thị đúng
- `status = SUBMITTED` (chờ giáo viên chấm)
- `score`, `pointsEarned`, `isCorrect` đều là `null` cho đến khi giáo viên chấm

---

### Bước 5: Xem danh sách submissions

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/writing-submissions`

**Quyền:** `TEACHER` (chỉ xem, không chấm điểm)

**Response:**
```json
[
  {
    "submissionId": 1,
    "assignmentId": 5,
    "studentId": 5,
    "studentName": "Nguyễn Văn A",
    "studentEmail": "student@example.com",
    "submittedAt": "2026-02-15T10:30:00",
    "score": null,
    "maxScore": 50,
    "status": "SUBMITTED",
    "feedback": null,
    "answers": [...]
  },
  {
    "submissionId": 2,
    "assignmentId": 5,
    "studentId": 6,
    "studentName": "Trần Thị B",
    "studentEmail": "student2@example.com",
    "submittedAt": "2026-02-15T11:00:00",
    "score": null,
    "maxScore": 50,
    "status": "SUBMITTED",
    "feedback": null,
    "answers": [...]
  }
]
```

**Lưu ý:**
- COURSE_MANAGER và ADMIN chỉ xem được, không chấm điểm
- TEACHER có thể xem để chuẩn bị chấm điểm

---

### Bước 6: TEACHER xem chi tiết submission để chấm điểm

**Endpoint:** `GET /api/v1/assignments/writing-submissions/{submissionId}`

**Quyền:** Chỉ `TEACHER` (teacher của khóa học đó)

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 5,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": null,
  "maxScore": 50,
  "status": "SUBMITTED",
  "feedback": null,
  "answers": [
    {
      "answerId": 1,
      "questionId": 1,
      "questionText": "わたし（　）学生です。",
      "questionType": "FILL_BLANK",
      "studentAnswer": "は",
      "points": 2,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "は"
    },
    {
      "answerId": 7,
      "questionId": 7,
      "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
      "questionType": "ESSAY_WRITING",
      "studentAnswer": "私の家族は4人です。...",
      "points": 20,
      "pointsEarned": null,
      "isCorrect": null,
      "sampleAnswer": "私の家族は4人です。..."
    }
  ]
}
```

**Lưu ý:**
- Chỉ TEACHER mới xem được chi tiết để chấm điểm
- TEACHER phải là teacher của khóa học chứa assignment đó
- Giáo viên xem được: bài làm của học sinh, đáp án mẫu (sampleAnswer), điểm tối đa (points)

---

### Bước 7: TEACHER chấm điểm

**Endpoint:** `POST /api/v1/assignments/writing-submissions/{submissionId}/grade`

**Quyền:** Chỉ `TEACHER` (teacher của khóa học đó)

**Request Body:**
```json
{
  "score": 45,
  "feedback": "Làm tốt! Cần chú ý thêm về cách sử dụng trợ từ và ngữ pháp.",
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
    },
    {
      "answerId": 4,
      "pointsEarned": 3,
      "isCorrect": true
    },
    {
      "answerId": 5,
      "pointsEarned": 2,
      "isCorrect": false
    },
    {
      "answerId": 6,
      "pointsEarned": 5,
      "isCorrect": true
    },
    {
      "answerId": 7,
      "pointsEarned": 18,
      "isCorrect": null
    }
  ]
}
```

**Lưu ý:**
- `score`: Tổng điểm giáo viên chấm (có thể khác với tổng `pointsEarned` nếu giáo viên muốn điều chỉnh)
- `pointsEarned`: Điểm đạt được cho từng câu (0 đến points tối đa)
- `isCorrect`: Đúng/sai cho từng câu (có thể `null` cho ESSAY_WRITING)
- `feedback`: Nhận xét tổng thể từ giáo viên (optional)

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 5,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": 45,
  "maxScore": 50,
  "status": "GRADED",
  "feedback": "Làm tốt! Cần chú ý thêm về cách sử dụng trợ từ và ngữ pháp.",
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
      "answerId": 7,
      "questionId": 7,
      "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
      "studentAnswer": "私の家族は4人です。...",
      "points": 20,
      "pointsEarned": 18,
      "isCorrect": null,
      "sampleAnswer": "私の家族は4人です。..."
    }
  ]
}
```

**Lưu ý:**
- Sau khi chấm, `status = GRADED`
- Học sinh có thể xem kết quả ngay sau khi giáo viên chấm
- Giáo viên có thể chấm lại để cập nhật điểm

---

### Bước 8: Học sinh xem kết quả

**Endpoint:** `GET /api/v1/assignments/{assignmentId}/writing-result`

**Quyền:** `STUDENT` hoặc `ADMIN`

**Response:**
```json
{
  "submissionId": 1,
  "assignmentId": 5,
  "studentId": 5,
  "studentName": "Nguyễn Văn A",
  "studentEmail": "student@example.com",
  "submittedAt": "2026-02-15T10:30:00",
  "score": 45,
  "maxScore": 50,
  "status": "GRADED",
  "feedback": "Làm tốt! Cần chú ý thêm về cách sử dụng trợ từ và ngữ pháp.",
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
    },
    {
      "answerId": 4,
      "questionId": 4,
      "questionText": "Sắp xếp các từ sau thành câu đúng:",
      "studentAnswer": "[\"わたし\",\"は\",\"学生\",\"です\"]",
      "points": 3,
      "pointsEarned": 3,
      "isCorrect": true,
      "sampleAnswer": "わたしは学生です"
    },
    {
      "answerId": 7,
      "questionId": 7,
      "questionText": "Viết một đoạn văn ngắn về chủ đề sau:",
      "studentAnswer": "私の家族は4人です。...",
      "points": 20,
      "pointsEarned": 18,
      "isCorrect": null,
      "sampleAnswer": "私の家族は4人です。..."
    }
  ]
}
```

**Lưu ý:**
- Chỉ xem được khi đã nộp bài
- Chỉ xem được khi giáo viên đã chấm (status = GRADED)
- Nếu chưa được chấm → Lỗi: "Bài làm của bạn chưa được giáo viên chấm điểm. Vui lòng chờ giáo viên chấm."
- Response bao gồm:
  - Bài làm của học sinh (`studentAnswer`)
  - Điểm từng câu (`pointsEarned`)
  - Điểm tổng (`score`)
  - Feedback từ giáo viên (`feedback`)
  - Đáp án mẫu (`sampleAnswer`) để so sánh

---

## Quy trình chấm điểm thủ công của TEACHER

### Cách giáo viên chấm điểm:

1. **Giáo viên xem submission** → Đọc từng câu trả lời của học sinh
2. **So sánh với đáp án mẫu** → Xem `sampleAnswer` để tham khảo
3. **Chấm từng câu** → Cho điểm từ 0 đến điểm tối đa (`points`)
4. **Tính tổng điểm** → Cộng tất cả `pointsEarned` (có thể điều chỉnh)
5. **Viết feedback** → Nhận xét tổng thể (optional)
6. **Gửi kết quả** → Hệ thống lưu và cập nhật `status = GRADED`

### Ví dụ chấm điểm:

| Câu | Loại | Điểm tối đa | Học sinh trả lời | Giáo viên chấm | Điểm đạt được |
|-----|------|-------------|------------------|----------------|---------------|
| 1 | FILL_BLANK | 2 | "は" | Đúng | 2 |
| 2 | FILL_BLANK | 2 | "が" | Sai (cho 1 điểm) | 1 |
| 3 | FILL_BLANK | 2 | "を" | Đúng | 2 |
| 4 | REORDER | 3 | ["わたし","は","学生","です"] | Đúng | 3 |
| 5 | REORDER | 3 | ["わたし","は","パン","を"] | Thiếu từ | 2 |
| 6 | MATCHING | 5 | 1-a, 2-b, 3-c, 4-d | Đúng hết | 5 |
| 7 | ESSAY_WRITING | 20 | Bài văn 120 từ | Tốt, có lỗi nhỏ | 18 |
| **Tổng** | | **37** | | | **33** |

**Request chấm điểm:**
```json
{
  "score": 33,
  "feedback": "Làm tốt! Cần chú ý thêm về cách sử dụng trợ từ.",
  "answerGrades": [
 
    {"answerId": 6, "pointsEarned": 5, "isCorrect": true},
    {"answerId": 7, "pointsEarned": 18, "isCorrect": null}
  ]
}
```

---


## Tóm tắt Endpoints

| Endpoint | Method | Quyền | Mô tả |
|----------|--------|-------|-------|
| `/api/v1/assignments/courses/assignments` | POST | COURSE_MANAGER, ADMIN | Tạo assignment (với `assignmentType: "WRITING"`) |
| `/api/v1/assignments/{assignmentId}/writing-questions` | POST | COURSE_MANAGER, ADMIN | Tạo câu hỏi (FILL_BLANK, REORDER, MATCHING, ESSAY_WRITING) |
| `/api/v1/assignments/{assignmentId}/questions` | GET | Tất cả | Xem danh sách câu hỏi |
| `/api/v1/assignments/{assignmentId}/submit-writing` | POST | STUDENT, ADMIN | Học sinh nộp bài |
| `/api/v1/assignments/{assignmentId}/writing-submissions` | GET | COURSE_MANAGER, ADMIN, TEACHER | Xem danh sách submissions (chỉ xem) |
| `/api/v1/assignments/writing-submissions/{submissionId}` | GET | **Chỉ TEACHER** | **TEACHER xem chi tiết để chấm điểm** |
| `/api/v1/assignments/writing-submissions/{submissionId}/grade` | POST | **Chỉ TEACHER** | **TEACHER chấm điểm** |
| `/api/v1/assignments/{assignmentId}/writing-result` | GET | STUDENT, ADMIN | **Học sinh xem kết quả sau khi được chấm** |

---

## Luồng hoạt động hoàn chỉnh

1. **COURSE_MANAGER** tạo assignment: `POST /api/v1/assignments/courses/assignments` (với `assignmentType: "WRITING"`)
2. **COURSE_MANAGER** tạo câu hỏi: `POST /api/v1/assignments/5/writing-questions` (tạo từng câu)
3. **STUDENT** xem câu hỏi: `GET /api/v1/assignments/5/questions`
4. **STUDENT** làm và nộp bài: `POST /api/v1/assignments/5/submit-writing`
   - Status = `SUBMITTED`, chờ giáo viên chấm
5. **TEACHER** xem danh sách submissions: `GET /api/v1/assignments/5/writing-submissions`
6. **TEACHER** xem chi tiết và chấm điểm: 
   - `GET /api/v1/assignments/writing-submissions/1`
   - `POST /api/v1/assignments/writing-submissions/1/grade`
   - Status = `GRADED`
7. **STUDENT** xem kết quả: `GET /api/v1/assignments/5/writing-result`
   - Thấy điểm số, feedback, và bài làm đã được chấm

---

## Lưu ý quan trọng

1. **Tạo assignment trước**: Phải tạo assignment với `assignmentType: "WRITING"` trước khi tạo câu hỏi
2. **Tạo từng câu hỏi một**: Mỗi lần gọi API tạo một câu hỏi
3. **orderIndex quyết định thứ tự**: Câu hỏi sẽ hiển thị theo `orderIndex` tăng dần
4. **Có thể tạo nhiều câu cùng loại**: Một đề có thể có nhiều câu FILL_BLANK, nhiều câu REORDER, v.v.
5. **points có thể khác nhau**: Tùy độ khó, mỗi câu có thể có điểm khác nhau
6. **sampleAnswer giúp giáo viên**: Đáp án mẫu giúp giáo viên tham khảo khi chấm
7. **Chỉ TEACHER chấm điểm**: COURSE_MANAGER và ADMIN chỉ xem được, không chấm điểm
8. **TEACHER phải là teacher của course**: Hệ thống kiểm tra TEACHER có phải là teacher của khóa học đó không
9. **REORDER và MATCHING lưu dạng JSON**: Frontend cần parse JSON để hiển thị đúng
10. **Học sinh chỉ xem được sau khi được chấm**: Endpoint `/writing-result` chỉ trả về khi status = GRADED



---

## Trạng thái Submission

- **SUBMITTED**: Học sinh đã nộp bài, đang chờ giáo viên chấm
- **GRADED**: Giáo viên đã chấm điểm, học sinh có thể xem kết 