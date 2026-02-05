package com.example.online_learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.online_learning.dto.response.LearningProcessDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.LearningProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-process")
@RequiredArgsConstructor
@Tag(name = "Learning Process", description = "Quản lý tiến trình học tập của học viên")
public class LearningProcessController {

    private final LearningProcessService learningProcessService;

    /**
     * 1️⃣ User enroll khóa học → tạo learning process
     */
    @Operation(
            summary = "Enroll khóa học",
            description = """
                    Được gọi khi học viên đăng ký (enroll) một khóa học.
                    - Tạo learning process cho user & course
                    - Chỉ tạo 1 lần duy nhất
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Enroll thành công"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User đã enroll khóa học này"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy khóa học"
            )
    })
    @PostMapping("/enroll")
    public ResponseEntity<Void> createLearningProcess(
            @RequestParam Long courseId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        learningProcessService.createLearningProcess(courseId, userDetail);
        return ResponseEntity.ok().build();
    }

    /**
     * 2️⃣ FE lấy progress để hiển thị
     */
    @Operation(
            summary = "Lấy tiến trình học tập",
            description = """
                    FE gọi API này để hiển thị tiến trình học tập của user:
                    - % hoàn thành
                    - lesson đã học
                    - trạng thái khóa học
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy tiến trình thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LearningProcessDtoRes.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy learning process"
            )
    })
    @GetMapping
    public ResponseEntity<LearningProcessDtoRes> getLearningProcess(
            @RequestParam Long courseId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                learningProcessService.getByCourseAndUser(courseId, userDetail)
        );
    }

    /**
     * 3️⃣ Check khóa học đã hoàn thành chưa
     */
    @Operation(
            summary = "Kiểm tra khóa học đã hoàn thành",
            description = """
                    Kiểm tra user đã hoàn thành toàn bộ lesson trong khóa học hay chưa.
                    - true: đã hoàn thành
                    - false: chưa hoàn thành
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Kiểm tra thành công"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy khóa học hoặc learning process"
            )
    })
    @GetMapping("/completed")
    public ResponseEntity<Boolean> isCourseCompleted(
            @RequestParam Long courseId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                learningProcessService.isCourseCompleted(courseId, userDetail)
        );
    }
}
