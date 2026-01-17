package com.example.online_learning.service;

import com.example.online_learning.entity.Feedback;
import com.example.online_learning.security.CustomUserDetail;

public interface AiPromptService {
    Feedback generateFeedbackPrompt(Long submissionId, CustomUserDetail userDetail);
    String generateHintLessonPrompt(Long lessonId);
    String generateQuizPracticePrompt(Long lessonId);
}
