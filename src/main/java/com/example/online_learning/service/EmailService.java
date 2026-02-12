package com.example.online_learning.service;

import com.example.online_learning.dto.response.WritingAnswerDetailDtoRes;
import java.util.List;

public interface EmailService {
    void sendWritingAssignmentResult(String toEmail, String studentName, 
                                     String assignmentTitle, 
                                     Integer score, Integer maxScore, 
                                     String feedback,
                                     List<WritingAnswerDetailDtoRes> answers);
}
