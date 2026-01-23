package com.example.online_learning.controller;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentService;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final AssignmentSubmissionService submissionService;
    private final FeedbackService feedbackService;
    public AssignmentController(AssignmentService assignmentService, AssignmentSubmissionService submissionService, FeedbackService feedbackService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.feedbackService = feedbackService;
    }
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<?> createAssignment(
            @RequestBody AssignmentDtoReq request
    ) {
        return ResponseEntity.ok(
                assignmentService.createAssignment(request)
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

}
