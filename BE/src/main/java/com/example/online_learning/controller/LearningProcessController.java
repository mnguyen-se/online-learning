package com.example.online_learning.controller;


import com.example.online_learning.dto.response.LearningProcessDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.LearningProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        learningProcessService.createLearningProcess(courseId, userDetail);
        return ResponseEntity.ok().build();
    }

    /**
     * FE lấy progress để hiển thị
     */
    @GetMapping
    public ResponseEntity<LearningProcessDtoRes> getLearningProcess(
            @RequestParam Long courseId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                learningProcessService.getByCourseAndUser(courseId, userDetail)
        );
    }

    /**
     * Check course đã hoàn thành chưa
     */
    @GetMapping("/completed")
    public ResponseEntity<Boolean> isCourseCompleted(
            @RequestParam Long courseId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                learningProcessService.isCourseCompleted(courseId, userDetail)
        );
    }
}

