package com.example.online_learning.controller;

import com.example.online_learning.dto.request.GradeQuizSubmissionDtoReq;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignment Quiz", description = "Quản lý assignment dạng quiz")
public class AssignmentQuizController {

    private final AssignmentSubmissionService submissionService;
    private final QuestionService questionService;

    public AssignmentQuizController(AssignmentSubmissionService submissionService, QuestionService questionService) {
        this.submissionService = submissionService;
        this.questionService = questionService;
    }

    @Operation(
            summary = "Submit quiz",
            description = "Học sinh nộp đáp án quiz và chờ giáo viên chấm."
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @PostMapping("/{assignmentId}/submit-quiz")
    public ResponseEntity<?> submitQuizAnswers(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody SubmitAnswersDtoReq request
    ) {
        return ResponseEntity.ok(
                submissionService.submitQuizAnswers(assignmentId, userDetail, request)
        );
    }

    @Operation(
            summary = "Xem kết quả quiz",
            description = "Lấy kết quả quiz của học sinh"
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping("/{assignmentId}/quiz-result")
    public ResponseEntity<?> getQuizResult(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                submissionService.getQuizResult(assignmentId, userDetail)
        );
    }

    @Operation(
            summary = "Xem danh sách submissions của quiz",
            description = "COURSE_MANAGER / ADMIN / TEACHER xem tất cả submissions của học sinh"
    )
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN','TEACHER')")
    @GetMapping("/{assignmentId}/quiz-submissions")
    public ResponseEntity<?> getQuizSubmissions(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                submissionService.getQuizSubmissions(assignmentId, userDetail)
        );
    }

    @Operation(
            summary = "Giáo viên xem chi tiết quiz submission để chấm điểm",
            description = "Chỉ TEACHER mới xem được chi tiết submission quiz"
    )
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/quiz-submissions/{submissionId}")
    public ResponseEntity<?> getQuizSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                submissionService.getQuizSubmission(submissionId, userDetail)
        );
    }

    @Operation(
            summary = "Giáo viên chấm điểm quiz submission",
            description = "Giáo viên nhập điểm và feedback cho quiz submission"
    )
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/quiz-submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeQuizSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody GradeQuizSubmissionDtoReq request
    ) {
        return ResponseEntity.ok(
                submissionService.gradeQuizSubmission(submissionId, userDetail, request)
        );
    }

    @Operation(
            summary = "Lấy danh sách câu hỏi",
            description = "Lấy tất cả câu hỏi của assignment"
    )
    @GetMapping("/{assignmentId}/questions")
    public ResponseEntity<?> getQuestionsByAssignmentId(
            @PathVariable Long assignmentId
    ) {
        return ResponseEntity.ok(
                questionService.getQuestionsByAssignmentId(assignmentId)
        );
    }

    @Operation(
            summary = "Upload câu hỏi bằng Excel",
            description = """
                    Upload file Excel để tạo câu hỏi quiz.
                    - Định dạng: .xls hoặc .xlsx
                    - Tối đa 30 câu hỏi
                    """
    )
    @PreAuthorize("hasRole('COURSE_MANAGER')")
    @PostMapping(
            value = "/{assignmentId}/questions/upload-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadQuestionsFromExcel(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                questionService.uploadQuestionsFromExcel(assignmentId, file)
        );
    }
}
