package com.example.online_learning.service;

import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.dto.response.QuizResultDtoRes;
import com.example.online_learning.security.CustomUserDetail;

public interface AssignmentSubmissionService {
    public void submit(Long assignmentId, CustomUserDetail userDetail, String content);
    QuizResultDtoRes submitQuizAnswers(Long assignmentId, CustomUserDetail userDetail, SubmitAnswersDtoReq request);
    QuizResultDtoRes getQuizResult(Long assignmentId, CustomUserDetail userDetail);
}
