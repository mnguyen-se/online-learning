package com.example.online_learning.controller;

import com.example.online_learning.dto.request.GradeWritingSubmissionDtoReq;
import com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq;
import com.example.online_learning.dto.request.WritingQuestionDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignment Writing", description = "Quản lý assignment dạng writing")
public class AssignmentWritingController {

    private final AssignmentSubmissionService submissionService;
    private final QuestionService questionService;

    public AssignmentWritingController(AssignmentSubmissionService submissionService, QuestionService questionService) {
        this.submissionService = submissionService;
        this.questionService = questionService;
    }

    @Operation(
            summary = "Tạo câu hỏi điền vào chỗ trống",
            description = "COURSE_MANAGER tạo câu hỏi fill-in-the-blank cho writing assignment"
    )
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/{assignmentId}/writing-questions")
    public ResponseEntity<?> createWritingQuestion(
            @PathVariable Long assignmentId,
            @RequestBody WritingQuestionDtoReq request
    ) {
        return ResponseEntity.ok(
                questionService.createWritingQuestion(assignmentId, request)
        );
    }

    @Operation(
            summary = "Lấy danh sách câu hỏi writing",
            description = "Lấy tất cả câu hỏi của assignment writing"
    )
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN','TEACHER')")
    @GetMapping("/{assignmentId}/writing-questions")
    public ResponseEntity<?> getWritingQuestionsByAssignmentId(
            @PathVariable Long assignmentId
    ) {
        return ResponseEntity.ok(
                questionService.getWritingQuestionsByAssignmentId(assignmentId)
        );
    }

    @Operation(
            summary = "Học sinh nộp bài writing assignment",
            description = "Học sinh nộp đáp án điền vào chỗ trống. Giáo viên sẽ chấm thủ công sau đó."
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @PostMapping("/{assignmentId}/submit-writing")
    public ResponseEntity<?> submitWritingAnswers(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody SubmitWritingAnswersDtoReq request
    ) {
        return ResponseEntity.ok(
                submissionService.submitWritingAnswers(assignmentId, userDetail, request)
        );
    }

    @Operation(
            summary = "Xem danh sách submissions của writing assignment",
            description = "COURSE_MANAGER / ADMIN / TEACHER xem tất cả submissions của học sinh (chỉ xem, không chấm điểm)"
    )
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN','TEACHER')")
    @GetMapping("/{assignmentId}/writing-submissions")
    public ResponseEntity<?> getWritingSubmissions(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                submissionService.getWritingSubmissions(assignmentId, userDetail)
        );
    }

    @Operation(
            summary = "Giáo viên xem chi tiết submission để chấm điểm",
            description = "Chỉ TEACHER mới xem được chi tiết submission để chấm điểm"
    )
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/writing-submissions/{submissionId}")
    public ResponseEntity<?> getWritingSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                submissionService.getWritingSubmission(submissionId, userDetail)
        );
    }

    @Operation(
            summary = "Giáo viên chấm điểm writing assignment",
            description = "Chỉ TEACHER mới được chấm điểm thủ công cho writing assignment submission"
    )
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/writing-submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeWritingSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody GradeWritingSubmissionDtoReq request
    ) {
        return ResponseEntity.ok(
                submissionService.gradeWritingSubmission(submissionId, userDetail, request)
        );
    }

    @Operation(
            summary = "Học sinh xem kết quả writing assignment",
            description = "Học sinh xem kết quả sau khi giáo viên đã chấm điểm. Chỉ xem được khi status = GRADED"
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping("/{assignmentId}/writing-result")
    public ResponseEntity<?> getWritingResult(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                submissionService.getWritingResult(assignmentId, userDetail)
        );
    }
}
