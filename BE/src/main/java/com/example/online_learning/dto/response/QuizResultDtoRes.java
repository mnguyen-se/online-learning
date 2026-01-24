package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDtoRes {
    private Long submissionId;
    private Long assignmentId;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer score;
    private Integer maxScore;
    private Double percentage;
    private List<AnswerDetailDtoRes> details;
}
