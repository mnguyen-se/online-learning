package com.example.online_learning.service;

import com.example.online_learning.entity.Feedback;
import com.example.online_learning.security.CustomUserDetail;

public interface FeedbackService {
    public void gradeSubmission(
            Long submissionId,
            CustomUserDetail userDetail,
            Integer score,
            String comment
    );

    public Feedback gradeSubmissionWithAI(
            Long submissionId,
            CustomUserDetail userDetail,
            Integer score,
            String comment
    );


}
