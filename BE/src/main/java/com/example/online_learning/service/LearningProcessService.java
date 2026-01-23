package com.example.online_learning.service;

import com.example.online_learning.dto.response.LearningProcessDtoRes;
import com.example.online_learning.security.CustomUserDetail;

public interface LearningProcessService {

    // Khi user enroll course
    void createLearningProcess(Long courseId, CustomUserDetail userDetail);

    // +1 khi hoàn thành lesson / assignment
    void increaseProgress(Long courseId, CustomUserDetail userDetail);

    // FE lấy progress
    LearningProcessDtoRes getByCourseAndUser(Long courseId, CustomUserDetail userDetail);

    // Check hoàn thành
    boolean isCourseCompleted(Long courseId, CustomUserDetail userDetail);
}

