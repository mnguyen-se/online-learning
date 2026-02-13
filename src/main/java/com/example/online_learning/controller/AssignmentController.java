package com.example.online_learning.controller;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.request.GradeWritingSubmissionDtoReq;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq;
import com.example.online_learning.dto.request.WritingQuestionDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentService;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignment", description = "Quản lý bài tập, quiz và submission")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentSubmissionService submissionService;
    private final QuestionService questionService;

    public AssignmentController(
            AssignmentService assignmentService,
            AssignmentSubmissionService submissionService,
            QuestionService questionService
    ) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.questionService = questionService;
    }

    @Operation(
            summary = "Tạo assignment",
            description = "COURSE_MANAGER / ADMIN tạo assignment cho một khóa học"
    )
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/courses/assignments")
    public ResponseEntity<?> createAssignment(
             @RequestBody AssignmentDtoReq request
    ) {
        return ResponseEntity.ok(
                assignmentService.createAssignment(request)
        );
    }

    @Operation(
            summary = "Submit quiz",
            description = "Học sinh nộp đáp án quiz và nhận kết quả ngay lập tức. Có thể nộp lại để cải thiện điểm số."
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
            summary = "Xem assignment",
            description = "Lấy thông tin assignment theo assignmentId"
    )
    @GetMapping("/get/{assignmentId}")
    public ResponseEntity<?> viewAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(
                assignmentService.viewAssignment(assignmentId)
        );
    }

    @Operation(
            summary = "Lấy assignment theo course",
            description = "Lấy danh sách assignment thuộc một khóa học"
    )
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> viewAssignmentByCourseId(
            @PathVariable long courseId
    ) {
        return ResponseEntity.ok(
                assignmentService.findByCourseId(courseId)
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

    // ========== WRITING ASSIGNMENT ENDPOINTS ==========

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
