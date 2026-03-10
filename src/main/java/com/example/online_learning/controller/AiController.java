package com.example.online_learning.controller;

import com.example.online_learning.entity.Feedback;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AiPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(
        name = "AI API",
        description = "Các API AI hỗ trợ học tập: feedback, hint bài học, quiz practice"
)
public class AiController {

    private final AiPromptService aiPromptService;

    // ================= AI FEEDBACK FOR SUBMISSION =================
    @Operation(
            summary = "AI tạo feedback cho bài nộp",
            description = """
                    API cho phép AI tạo feedback tự động cho assignment submission.
                    
                    🔐 Chỉ TEACHER hoặc ADMIN được phép gọi.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo feedback thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Feedback.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy submission"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "500", description = "Lỗi khi AI sinh feedback")
    })
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping("/submissions/{submissionId}/feedback")
    public ResponseEntity<Feedback> generateFeedback(
            @Parameter(
                    description = "ID của submission",
                    example = "100",
                    required = true
            )
            @PathVariable Long submissionId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                aiPromptService.generateFeedbackPrompt(submissionId, userDetail)
        );
    }

    // ================= AI LESSON HINT =================
    @Operation(
            summary = "AI tạo hint giải thích bài học",
            description = """
                    API cho phép AI tạo hint / gợi ý giải thích cho bài học.
                    
                    🔐 STUDENT hoặc ADMIN được phép gọi.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo hint thành công",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lesson"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "500", description = "Lỗi khi AI sinh hint")
    })
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping("/lessons/{lessonId}/hint")
    public ResponseEntity<String> generateLessonHint(
            @Parameter(
                    description = "ID của lesson",
                    example = "20",
                    required = true
            )
            @PathVariable Long lessonId
    ) {
        return ResponseEntity.ok(
                aiPromptService.generateHintLessonPrompt(lessonId)
        );
    }

    // ================= AI QUIZ PRACTICE =================
    @Operation(
            summary = "AI tạo quiz practice dạng HTML",
            description = """
                    API cho phép AI tạo quiz practice cho bài học dưới dạng HTML.
                    
                    📌 Frontend có thể:
                    - Render trực tiếp
                    - Mở tab mới
                    - Nhúng iframe
                    
                    🔐 STUDENT hoặc ADMIN được phép gọi.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo quiz practice thành công",
                    content = @Content(
                            mediaType = MediaType.TEXT_HTML_VALUE
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lesson"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "500", description = "Lỗi khi AI sinh quiz")
    })
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping(
            value = "/lessons/{lessonId}/quiz",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> generateQuizPractice(
            @Parameter(
                    description = "ID của lesson",
                    example = "20",
                    required = true
            )
            @PathVariable Long lessonId
    ) {

        String quizHtml = aiPromptService.generateQuizPracticePrompt(lessonId);

        String quizLink = "http://localhost:8080/api/v1/ai/lessons/" + lessonId + "/quiz";

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
                "        </div>\n" +
                quizHtml + "\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok(fullHtml);
    }
}
