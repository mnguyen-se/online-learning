package com.example.online_learning.service;

import com.example.online_learning.dto.request.GradeQuizSubmissionDtoReq;
import com.example.online_learning.dto.request.GradeWritingSubmissionDtoReq;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq;
import com.example.online_learning.dto.response.QuizResultDtoRes;
import com.example.online_learning.dto.response.QuizSubmissionDtoRes;
import com.example.online_learning.dto.response.WritingSubmissionDtoRes;
import com.example.online_learning.security.CustomUserDetail;

import java.util.List;

public interface AssignmentSubmissionService {
    QuizResultDtoRes submitQuizAnswers(Long assignmentId, CustomUserDetail userDetail, SubmitAnswersDtoReq request);
    QuizResultDtoRes getQuizResult(Long assignmentId, CustomUserDetail userDetail);
    List<QuizSubmissionDtoRes> getQuizSubmissions(Long assignmentId, CustomUserDetail userDetail);
    QuizSubmissionDtoRes getQuizSubmission(Long submissionId, CustomUserDetail userDetail);
    QuizResultDtoRes gradeQuizSubmission(Long submissionId, CustomUserDetail userDetail, GradeQuizSubmissionDtoReq request);
    WritingSubmissionDtoRes submitWritingAnswers(Long assignmentId, CustomUserDetail userDetail, SubmitWritingAnswersDtoReq request);
    List<WritingSubmissionDtoRes> getWritingSubmissions(Long assignmentId, CustomUserDetail userDetail);
    WritingSubmissionDtoRes getWritingSubmission(Long submissionId, CustomUserDetail userDetail);
    WritingSubmissionDtoRes gradeWritingSubmission(Long submissionId, CustomUserDetail userDetail, GradeWritingSubmissionDtoReq request);
    WritingSubmissionDtoRes getWritingResult(Long assignmentId, CustomUserDetail userDetail);
}
