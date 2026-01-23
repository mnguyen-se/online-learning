package com.example.online_learning.controller;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentService;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.FeedbackService;
import com.example.online_learning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final AssignmentSubmissionService submissionService;
    private final FeedbackService feedbackService;
    private final QuestionService questionService;
    
    public AssignmentController(AssignmentService assignmentService, 
                               AssignmentSubmissionService submissionService, 
                               FeedbackService feedbackService,
                               QuestionService questionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.feedbackService = feedbackService;
        this.questionService = questionService;
    }
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<?> createAssignment(
            @PathVariable Long courseId,
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid AssignmentDtoReq request
    ) {
        return ResponseEntity.ok(
                assignmentService.createAssignment(courseId, request)
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody String content
    ) {
        submissionService.submit(assignmentId, userDetail, content);
        return ResponseEntity.ok(
                "Submit assignment successfully!"
        );
    }

    @Operation(
            summary = "Submit quiz answers and get result",
            description = "Submit answers for quiz questions and get automatic grading result"
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @PostMapping("/{assignmentId}/submit-quiz")
    public ResponseEntity<?> submitQuizAnswers(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @org.springframework.web.bind.annotation.RequestBody SubmitAnswersDtoReq request
    ) {
        return ResponseEntity.ok(submissionService.submitQuizAnswers(assignmentId, userDetail, request));
    }

    @Operation(
            summary = "Get quiz result",
            description = "Get quiz result for a submitted assignment"
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping("/{assignmentId}/quiz-result")
    public ResponseEntity<?> getQuizResult(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(submissionService.getQuizResult(assignmentId, userDetail));
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> grade(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestParam Integer score,
            @RequestBody String comment
    ) {
        feedbackService.gradeSubmission(submissionId, userDetail, score, comment);
        return ResponseEntity.ok(
                "Grade submission successfully!"
        );
    }

    @GetMapping("/get/{assignmentId}")
    public ResponseEntity<?> viewAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok().body(assignmentService.viewAssignment(assignmentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> viewAssignmentByCourseId(@PathVariable long courseId) {
        return ResponseEntity.ok().body(assignmentService.findByCourseId(courseId));
    }

    /**
     * Lấy danh sách câu hỏi theo assignmentId
     */
    @Operation(
            summary = "Get all questions for an assignment",
            description = "Retrieve all questions associated with a specific assignment, ordered by orderIndex"
    )
    @GetMapping("/{assignmentId}/questions")
    public ResponseEntity<?> getQuestionsByAssignmentId(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId
    ) {
        return ResponseEntity.ok(questionService.getQuestionsByAssignmentId(assignmentId));
    }

    @Operation(
            summary = "Upload Excel file to create questions for assignment",
            description = "Upload an Excel file (.xlsx or .xls) containing questions. " +
                    "File format: 6 columns (Question, Option A, Option B, Option C, Option D, Correct Answer). " +
                    "Maximum 30 questions allowed."
    )
    @PreAuthorize("hasRole('COURSE_MANAGER')")
    @PostMapping(
            value = "/{assignmentId}/questions/upload-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadQuestionsFromExcel(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId,
            @Parameter(
                    description = "Excel file containing questions (.xlsx or .xls format)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(questionService.uploadQuestionsFromExcel(assignmentId, file));
    }

}
