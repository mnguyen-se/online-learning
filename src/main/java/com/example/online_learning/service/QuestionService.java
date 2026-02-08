package com.example.online_learning.service;

import com.example.online_learning.dto.response.ExcelUploadResponseDto;
import com.example.online_learning.dto.response.QuestionDtoRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    ExcelUploadResponseDto uploadQuestionsFromExcel(Long assignmentId, MultipartFile file);
    List<QuestionDtoRes> getQuestionsByAssignmentId(Long assignmentId);
}
