package com.example.online_learning.service;

import com.example.online_learning.entity.Feedback;

public interface FeedbackService {
    public Feedback gradeSubmission(
            Long submissionId,
            Long teacherId,
            Integer score,
            String comment
    );

    public Feedback gradeSubmissionWithAI(
            Long submissionId,
            Long teacherId,
            Integer score,
            String comment
    );


}
