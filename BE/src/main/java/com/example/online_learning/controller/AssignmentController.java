package com.example.online_learning.controller;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.service.AssignmentService;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long userId,
            @RequestBody String content
    ) {
        return ResponseEntity.ok(
                submissionService.submit(assignmentId, userId, content)
        );
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> grade(
            @PathVariable Long submissionId,
            @RequestParam Long teacherId,
            @RequestParam Integer score,
            @RequestBody String comment
    ) {
        return ResponseEntity.ok(
                feedbackService.gradeSubmission(submissionId, teacherId, score, comment)
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
