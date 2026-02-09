package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDtoRes {
    private Long feedbackId;
    private Long courseId;
    private String courseTitle;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private String comment;
    private LocalDateTime createdAt;
}
