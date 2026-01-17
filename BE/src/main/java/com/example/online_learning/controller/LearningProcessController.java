package com.example.online_learning.controller;


import com.example.online_learning.dto.response.LearningProcessDtoRes;
import com.example.online_learning.service.LearningProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-process")
@RequiredArgsConstructor
public class LearningProcessController {

    private final LearningProcessService learningProcessService;

    /**
     * Gọi khi user enroll course
     */
    @PostMapping("/enroll")
    public ResponseEntity<Void> createLearningProcess(
            @RequestParam Long courseId,
            @RequestParam Long userId
    ) {
        learningProcessService.createLearningProcess(courseId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * FE lấy progress để hiển thị
     */
    @GetMapping
    public ResponseEntity<LearningProcessDtoRes> getLearningProcess(
            @RequestParam Long courseId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                learningProcessService.getByCourseAndUser(courseId, userId)
        );
    }

    /**
     * Check course đã hoàn thành chưa
     */
    @GetMapping("/completed")
    public ResponseEntity<Boolean> isCourseCompleted(
            @RequestParam Long courseId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                learningProcessService.isCourseCompleted(courseId, userId)
        );
    }
}

