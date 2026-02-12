package com.example.online_learning.service;

public interface EmailService {
    void sendWritingAssignmentResult(String toEmail, String studentName, 
                                     String assignmentTitle, 
                                     Integer score, Integer maxScore, 
                                     String feedback);
}
