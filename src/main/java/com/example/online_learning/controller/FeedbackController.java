package com.example.online_learning.controller;

import com.example.online_learning.dto.request.CreateCourseFeedbackDtoReq;
import com.example.online_learning.dto.response.FeedbackDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedbacks")
@Tag(name = "Feedback API", description = "Quản lý feedback từ teacher cho student")
public class FeedbackController {
    
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(
            summary = "Tạo feedback cho khóa học đã hoàn thành",
            description = """
                    API cho phép teacher gửi feedback cho student khi student đã hoàn thành khóa học.
                    
                    🔐 Chỉ TEACHER mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo feedback thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy course hoặc student")
    })
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/course")
    public ResponseEntity<FeedbackDtoRes> createCourseFeedback(
            @RequestBody CreateCourseFeedbackDtoReq request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail teacherDetail
    ) {
        return ResponseEntity.ok(feedbackService.createCourseFeedback(request, teacherDetail));
    }

    @Operation(
            summary = "Lấy danh sách feedback của student",
            description = """
                    API lấy tất cả feedback mà student đã nhận được.
                    
                    🔐 STUDENT, TEACHER, ADMIN đều có thể truy cập.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách feedback thành công")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<FeedbackDtoRes>> getFeedbacksByStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByStudent(studentId));
    }

    @Operation(
            summary = "Lấy danh sách feedback của course",
            description = """
                    API lấy tất cả feedback của một course.
                    
                    🔐 TEACHER, ADMIN mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách feedback thành công")
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<FeedbackDtoRes>> getFeedbacksByCourse(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByCourse(courseId));
    }

    @Operation(
            summary = "Lấy danh sách feedback của teacher",
            description = """
                    API lấy tất cả feedback mà teacher đã gửi.
                    
                    🔐 Chỉ TEACHER mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách feedback thành công")
    })
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/my-feedbacks")
    public ResponseEntity<List<FeedbackDtoRes>> getMyFeedbacks(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail teacherDetail
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByTeacher(teacherDetail));
    }
}
