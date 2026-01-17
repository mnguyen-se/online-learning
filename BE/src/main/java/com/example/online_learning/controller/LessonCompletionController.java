package com.example.online_learning.controller;

import com.example.online_learning.service.LessonCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lessonsCompletion")
@RequiredArgsConstructor
public class LessonCompletionController {

    private final LessonCompletionService lessonCompletionService;

    @PostMapping("/{lessonId}/complete")
    public ResponseEntity<Void> completeLesson(
            @PathVariable Long lessonId,
            @RequestParam Long userId
    ) {
        lessonCompletionService.completeLesson(lessonId, userId);
        return ResponseEntity.ok().build();
    }
}

