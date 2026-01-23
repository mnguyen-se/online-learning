package com.example.online_learning.controller;

import com.example.online_learning.entity.Feedback;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AiPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiPromptService aiPromptService;

    /**
     * 1️⃣ AI tạo feedback comment cho assignment submission
     * GV hoặc hệ thống trigger
     */
    @PreAuthorize("hasAnyRole('TEACHER')")
    @PostMapping("/submissions/{submissionId}/feedback")
    public ResponseEntity<Feedback> generateFeedback(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                aiPromptService.generateFeedbackPrompt(submissionId,userDetail)
        );
    }

    /**
     * 2️⃣ AI tạo hint giải thích bài học
     * Học sinh dùng khi học lesson
     */
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/lessons/{lessonId}/hint")
    public ResponseEntity<String> generateLessonHint(
            @PathVariable Long lessonId
    ) {
        return ResponseEntity.ok(
                aiPromptService.generateHintLessonPrompt(lessonId)
        );
    }

    /**
     * 3️⃣ AI tạo quiz practice dạng HTML
     * FE render iframe hoặc mở tab mới
     */
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(
            value = "/lessons/{lessonId}/quiz",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> generateQuizPractice(
            @PathVariable Long lessonId
    ) {
        // 1️⃣ Lấy HTML quiz từ service
        String quizHtml = aiPromptService.generateQuizPracticePrompt(lessonId);

        // 2️⃣ Tạo link tự động cho iframe
        String quizLink = "http://localhost:8080/api/v1/ai/lessons/" + lessonId + "/quiz";

        // 3️⃣ Ghép HTML hoàn chỉnh bằng nối chuỗi (không dùng formatted)
        String fullHtml = "<!DOCTYPE html>\n" +
                "<html lang='vi'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <title>Quiz và Hướng Dẫn</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; background:#f4f4f9; padding:20px; line-height:1.6; }\n" +
                "        .container { max-width: 900px; margin:auto; background:#fff; padding:30px; border-radius:10px; box-shadow:0 0 15px rgba(0,0,0,0.1); }\n" +
                "        h1 { text-align:center; color:#d32f2f; margin-bottom:20px; }\n" +
                "        .hint { background:#e3f2fd; padding:15px; border-left:5px solid #2196f3; margin-bottom:20px; border-radius:5px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='container'>\n" +
                "        <h1>Quiz Practice</h1>\n" +
                "        <div class='hint'>\n" +
                "            <strong>Hướng dẫn:</strong><br>\n" +
                "            1. Copy hết tất cả code dưới dòng : Chèn quiz HTML do AI tạo ra.<br>\n" +
                "            2. Dán vào file text, lưu và nhập tên file. Sau đó đổi tên từ .txt sang .html.<br>\n" +
                "            3. Sau khi dán xong, nhấn \"Nộp Bài và Xem Kết Quả\" để xem điểm.<br>\n" +
                "            4. Học sinh nên ôn tập các kiến thức liên quan trước khi làm quiz.\n" +
                "        </div>\n" +
                "\n" +
                "        <!-- Chèn quiz HTML do AI tạo ra -->\n" +
                quizHtml + "\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok(fullHtml);
    }

}
