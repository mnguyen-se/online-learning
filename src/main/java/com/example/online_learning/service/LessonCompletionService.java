package com.example.online_learning.service;

import com.example.online_learning.security.CustomUserDetail;

public interface LessonCompletionService {
    void completeLesson(Long lessonId, CustomUserDetail userDetail);
}

