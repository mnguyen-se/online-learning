package com.example.online_learning.serviceImpl;

import com.example.online_learning.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendWritingAssignmentResult(String toEmail, String studentName, 
                                           String assignmentTitle, 
                                           Integer score, Integer maxScore, 
                                           String feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kết quả bài tập: " + assignmentTitle);
        
        String emailBody = String.format(
            "Xin chào %s,\n\n" +
            "Giáo viên đã chấm điểm cho bài tập của bạn:\n\n" +
            "📝 Bài tập: %s\n" +
            "📊 Điểm số: %d/%d\n" +
            "%s\n\n" +
            "Bạn có thể xem chi tiết kết quả tại hệ thống.\n\n" +
            "Trân trọng,\n" +
            "Hệ thống Online Learning",
            studentName,
            assignmentTitle,
            score,
            maxScore,
            feedback != null && !feedback.trim().isEmpty() ? "💬 Nhận xét: " + feedback + "\n" : ""
        );
        
        message.setText(emailBody);
        mailSender.send(message);
    }
}
