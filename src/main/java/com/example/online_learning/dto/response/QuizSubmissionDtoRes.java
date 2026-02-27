package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDtoRes {
    private Long submissionId;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private LocalDateTime submittedAt;
    private Integer score;
    private Integer maxScore;
    private String status;
    private String feedback;
    private List<AnswerDetailDtoRes> answers;
}
