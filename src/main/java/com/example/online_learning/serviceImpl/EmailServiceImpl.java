package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.response.AnswerDetailDtoRes;
import com.example.online_learning.dto.response.WritingAnswerDetailDtoRes;
import com.example.online_learning.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void sendWritingAssignmentResult(String toEmail, String studentName, 
                                           String assignmentTitle, 
                                           Integer score, Integer maxScore, 
                                           String feedback,
                                           List<WritingAnswerDetailDtoRes> answers) {
        System.out.println("=== START SENDING EMAIL ===");
        System.out.println("To Email: " + toEmail);
        System.out.println("Student Name: " + studentName);
        System.out.println("Assignment: " + assignmentTitle);
        System.out.println("Score: " + score + "/" + maxScore);
        
        if (mailSender == null) {
            System.err.println("ERROR: JavaMailSender is NULL!");
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kết quả bài tập: " + assignmentTitle);
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Xin chào ").append(studentName).append(",\n\n");
        emailBody.append("Giáo viên đã chấm điểm cho bài tập của bạn:\n\n");
        emailBody.append("📝 Bài tập: ").append(assignmentTitle).append("\n");
        emailBody.append("📊 Điểm số: ").append(score).append("/").append(maxScore).append("\n\n");
        
        if (feedback != null && !feedback.trim().isEmpty()) {
            emailBody.append("💬 Nhận xét: ").append(feedback).append("\n\n");
        }
        
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        emailBody.append("CHI TIẾT TỪNG CÂU HỎI:\n");
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        int questionNumber = 1;
        for (WritingAnswerDetailDtoRes answer : answers) {
            emailBody.append("Câu ").append(questionNumber).append(":\n");
            emailBody.append("Câu hỏi: ").append(answer.getQuestionText()).append("\n");
            emailBody.append("Đáp án của bạn: ").append(formatStudentAnswer(answer)).append("\n");
            
            if (answer.getSampleAnswer() != null && !answer.getSampleAnswer().trim().isEmpty()) {
                emailBody.append("Đáp án mẫu: ").append(answer.getSampleAnswer()).append("\n");
            }
            
            emailBody.append("Điểm: ").append(answer.getPointsEarned() != null ? answer.getPointsEarned() : 0)
                     .append("/").append(answer.getPoints()).append("\n");
            
            if (answer.getQuestionType() != com.example.online_learning.constants.QuestionType.ESSAY_WRITING
                    && answer.getIsCorrect() != null) {
                emailBody.append("Kết quả: ").append(answer.getIsCorrect() ? "✅ Đúng" : "❌ Sai").append("\n");
            }
            
            emailBody.append("\n");
            questionNumber++;
        }
        
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        emailBody.append("Tổng điểm: ").append(score).append("/").append(maxScore).append("\n\n");
        emailBody.append("Bạn có thể xem chi tiết kết quả tại hệ thống.\n\n");
        emailBody.append("Trân trọng,\n");
        emailBody.append("Hệ thống Online Learning");
        
        message.setText(emailBody.toString());
        
        try {
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void sendQuizResult(String toEmail, String studentName,
                               String assignmentTitle,
                               Integer score, Integer maxScore,
                               String feedback,
                               List<AnswerDetailDtoRes> answers) {
        System.out.println("=== START SENDING QUIZ EMAIL ===");
        System.out.println("To Email: " + toEmail);
        System.out.println("Student Name: " + studentName);
        System.out.println("Assignment: " + assignmentTitle);
        System.out.println("Score: " + score + "/" + maxScore);

        if (mailSender == null) {
            System.err.println("ERROR: JavaMailSender is NULL!");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kết quả quiz: " + assignmentTitle);

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Xin chào ").append(studentName).append(",\n\n");
        emailBody.append("Giáo viên đã chấm điểm cho quiz của bạn:\n\n");
        emailBody.append("Bài tập: ").append(assignmentTitle).append("\n");
        emailBody.append("Điểm số: ").append(score).append("/").append(maxScore).append("\n");

        if (feedback != null && !feedback.trim().isEmpty()) {
            emailBody.append("Nhận xét: ").append(feedback).append("\n");
        }

        emailBody.append("\nCHI TIẾT TỪNG CÂU HỎI:\n\n");

        int questionNumber = 1;
        for (AnswerDetailDtoRes answer : answers) {
            emailBody.append("Câu ").append(questionNumber).append(":\n");
            emailBody.append("Câu hỏi: ").append(answer.getQuestionText()).append("\n");
            emailBody.append("A: ").append(answer.getOptionA() != null ? answer.getOptionA() : "").append("\n");
            emailBody.append("B: ").append(answer.getOptionB() != null ? answer.getOptionB() : "").append("\n");
            emailBody.append("C: ").append(answer.getOptionC() != null ? answer.getOptionC() : "").append("\n");
            emailBody.append("D: ").append(answer.getOptionD() != null ? answer.getOptionD() : "").append("\n");
            emailBody.append("Đáp án của bạn: ").append(answer.getStudentAnswer()).append("\n");
            emailBody.append("Đáp án đúng: ").append(answer.getCorrectAnswer()).append("\n");
            emailBody.append("Điểm: ").append(answer.getPointsEarned() != null ? answer.getPointsEarned() : 0)
                    .append("/").append(answer.getPoints()).append("\n");
            if (answer.getIsCorrect() != null) {
                emailBody.append("Kết quả: ").append(answer.getIsCorrect() ? "Đúng" : "Sai").append("\n");
            }
            emailBody.append("\n");
            questionNumber++;
        }

        emailBody.append("Tổng điểm: ").append(score).append("/").append(maxScore).append("\n");

        message.setText(emailBody.toString());

        try {
            mailSender.send(message);
            System.out.println("QUIZ email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("QUIZ email sending failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private String formatStudentAnswer(WritingAnswerDetailDtoRes answer) {
        String studentAnswer = answer.getStudentAnswer();
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            return "(Chưa trả lời)";
        }
        
        if (answer.getQuestionType() == null) {
            return studentAnswer;
        }
        
        try {
            if (answer.getQuestionType() == com.example.online_learning.constants.QuestionType.REORDER) {
                List<String> items = objectMapper.readValue(studentAnswer, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                return String.join("", items);
            } else if (answer.getQuestionType() == com.example.online_learning.constants.QuestionType.MATCHING) {
                try {
                    List<com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq.MatchingPairDto> pairs = 
                        objectMapper.readValue(studentAnswer,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, 
                                com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq.MatchingPairDto.class));
                    
                    StringBuilder result = new StringBuilder();
                    for (com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq.MatchingPairDto pair : pairs) {
                        String aId = pair.getAId();
                        String bId = pair.getBId();
                        if (aId != null && bId != null && 
                            !aId.trim().isEmpty() && !bId.trim().isEmpty()) {
                            if (result.length() > 0) {
                                result.append(", ");
                            }
                            result.append(aId).append("-").append(bId);
                        }
                    }
                    return result.length() > 0 ? result.toString() : "(Chưa nối)";
                } catch (Exception e) {
                    try {
                        List<java.util.Map<String, Object>> pairs = objectMapper.readValue(studentAnswer,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, 
                                objectMapper.getTypeFactory().constructMapType(java.util.Map.class, String.class, Object.class)));
                        
                        StringBuilder result = new StringBuilder();
                        for (java.util.Map<String, Object> pair : pairs) {
                            String aId = getStringValue(pair, "aId", "aid", "AId", "AID");
                            String bId = getStringValue(pair, "bId", "bid", "BId", "BID");
                            if (aId != null && bId != null && 
                                !aId.equals("null") && !bId.equals("null") && 
                                !aId.trim().isEmpty() && !bId.trim().isEmpty()) {
                                if (result.length() > 0) {
                                    result.append(", ");
                                }
                                result.append(aId).append("-").append(bId);
                            }
                        }
                        return result.length() > 0 ? result.toString() : "(Chưa nối)";
                    } catch (Exception e2) {
                        return studentAnswer;
                    }
                }
            }
        } catch (Exception e) {
        }
        
        return studentAnswer;
    }
    
    private String getStringValue(java.util.Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                String strValue = value.toString();
                if (!strValue.equals("null") && !strValue.trim().isEmpty()) {
                    return strValue;
                }
            }
        }
        return null;
    }
}
