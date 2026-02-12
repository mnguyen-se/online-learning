package com.example.online_learning.controller;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.request.GradeSubmissionDtoReq;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentService;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.FeedbackService;
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
    private final FeedbackService feedbackService;
    private final QuestionService questionService;

    public AssignmentController(
            AssignmentService assignmentService,
            AssignmentSubmissionService submissionService,
            FeedbackService feedbackService,
            QuestionService questionService
    ) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.feedbackService = feedbackService;
        this.questionService = questionService;
    }

    /**
     * 1️⃣ Tạo assignment cho course
     */
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

    /**
     * 2️⃣ Submit assignment dạng text
     */
    @Operation(
            summary = "Submit assignment",
            description = "Học sinh nộp bài assignment dạng text"
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody String content
    ) {
        submissionService.submit(assignmentId, userDetail, content);
        return ResponseEntity.ok("Submit assignment successfully!");
    }

    /**
     * 3️⃣ Submit quiz - chờ giáo viên chấm
     */
    @Operation(
            summary = "Submit quiz",
            description = "Submit đáp án quiz, chờ giáo viên chấm điểm thủ công"
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

    /**
     * 4️⃣ Lấy kết quả quiz
     */
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

    /**
     * 5️⃣ Giáo viên chấm bài quiz
     */
    @Operation(
            summary = "Chấm bài quiz",
            description = "TEACHER / ADMIN chấm điểm quiz và nhận xét submission. Tự động tính isCorrect và pointsEarned. Luôn set status = GRADED."
    )
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeQuiz(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody GradeSubmissionDtoReq request
    ) {
        feedbackService.gradeQuizSubmission(
                submissionId,
                userDetail,
                request.getScore(),
                request.getComment()
        );
        return ResponseEntity.ok("Grade submission successfully!");
    }

    /**
     * 5b️⃣ Giáo viên xem danh sách bài nộp
     */
    @Operation(
            summary = "Xem danh sách bài nộp",
            description = "TEACHER / ADMIN xem danh sách tất cả bài nộp của một assignment"
    )
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<?> getSubmissionsByAssignment(
            @PathVariable Long assignmentId
    ) {
        return ResponseEntity.ok(
                submissionService.getSubmissionsByAssignment(assignmentId)
        );
    }

    /**
     * 5d️⃣ Giáo viên xem chi tiết bài làm
     */
    @Operation(
            summary = "Xem chi tiết bài làm",
            description = "TEACHER / ADMIN xem chi tiết bài làm của học sinh, bao gồm câu hỏi, đáp án, và feedback"
    )
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<?> getSubmissionDetail(
            @PathVariable Long submissionId
    ) {
        return ResponseEntity.ok(
                submissionService.getSubmissionDetail(submissionId)
        );
    }

    /**
     * 6️⃣ Xem assignment theo ID
     */
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

    /**
     * 7️⃣ Lấy assignment theo course
     */
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

    /**
     * 8️⃣ Lấy câu hỏi của assignment
     */
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

    /**
     * 9️⃣ Upload Excel tạo câu hỏi
     */
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
