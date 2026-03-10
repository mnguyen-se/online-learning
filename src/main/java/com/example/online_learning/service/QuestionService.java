package com.example.online_learning.service;

import com.example.online_learning.dto.request.WritingQuestionDtoReq;
import com.example.online_learning.dto.response.ExcelUploadResponseDto;
import com.example.online_learning.dto.response.QuestionDtoRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    ExcelUploadResponseDto uploadQuestionsFromExcel(Long assignmentId, MultipartFile file);
    List<QuestionDtoRes> getQuizQuestionsByAssignmentId(Long assignmentId);
    List<QuestionDtoRes> getWritingQuestionsByAssignmentId(Long assignmentId);
    List<QuestionDtoRes> getWritingQuestionsForStudent(Long assignmentId, Long studentId);
    QuestionDtoRes createWritingQuestion(Long assignmentId, WritingQuestionDtoReq request);
}
