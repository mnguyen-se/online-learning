package com.example.online_learning.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.LessonCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lessonsCompletion")
@RequiredArgsConstructor
@Tag(name = "Lesson Completion", description = "Đánh dấu hoàn thành bài học")
public class LessonCompletionController {

    private final LessonCompletionService lessonCompletionService;

    /**
     * 1️⃣ User hoàn thành lesson
     */
    @Operation(
            summary = "Hoàn thành bài học",
            description = """
                    Đánh dấu một lesson đã được user hoàn thành.
                    - Cập nhật lesson completion
                    - Tự động update learning process nếu cần
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đánh dấu hoàn thành lesson thành công"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Lesson đã được hoàn thành trước đó"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy lesson"
            )
    })
    @PostMapping("/{lessonId}/complete")
    public ResponseEntity<Void> completeLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        lessonCompletionService.completeLesson(lessonId, userDetail);
        return ResponseEntity.ok().build();
    }
}


