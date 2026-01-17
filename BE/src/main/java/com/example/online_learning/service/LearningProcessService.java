package com.example.online_learning.service;

import com.example.online_learning.dto.response.LearningProcessDtoRes;

public interface LearningProcessService {

    // Khi user enroll course
    void createLearningProcess(Long courseId, Long userId);

    // +1 khi hoàn thành lesson / assignment
    void increaseProgress(Long courseId, Long userId);

    // FE lấy progress
    LearningProcessDtoRes getByCourseAndUser(Long courseId, Long userId);

    // Check hoàn thành
    boolean isCourseCompleted(Long courseId, Long userId);
}

