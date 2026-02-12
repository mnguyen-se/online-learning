package com.example.online_learning.service;

import com.example.online_learning.dto.request.CreateCourseFeedbackDtoReq;
import com.example.online_learning.dto.response.FeedbackDtoRes;
import com.example.online_learning.entity.Feedback;
import com.example.online_learning.security.CustomUserDetail;

import java.util.List;

public interface FeedbackService {
    void gradeQuizSubmission(
            Long submissionId,
            CustomUserDetail userDetail,
            Integer score,
            String comment
    );

    public void requestRevision(
            Long submissionId,
            CustomUserDetail userDetail,
            String comment
    );

    public Feedback gradeSubmissionWithAI(
            Long submissionId,
            CustomUserDetail userDetail,
            Integer score,
            String comment
    );

    FeedbackDtoRes createCourseFeedback(CreateCourseFeedbackDtoReq request, CustomUserDetail teacherDetail);
    List<FeedbackDtoRes> getFeedbacksByStudent(Long studentId);
    List<FeedbackDtoRes> getFeedbacksByCourse(Long courseId);
    List<FeedbackDtoRes> getFeedbacksByTeacher(CustomUserDetail teacherDetail);
}
