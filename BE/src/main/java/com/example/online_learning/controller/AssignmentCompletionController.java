package com.example.online_learning.controller;

import com.example.online_learning.service.AssignmentCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentCompletionController {

    private final AssignmentCompletionService assignmentCompletionService;

    @PostMapping("/{assignmentId}/complete")
    public ResponseEntity<Void> completeAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long userId
    ) {
        assignmentCompletionService
                .completeAssignment(assignmentId, userId);
        return ResponseEntity.ok().build();
    }
}

