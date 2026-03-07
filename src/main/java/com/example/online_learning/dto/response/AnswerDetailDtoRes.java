package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDetailDtoRes {
    private Long answerId;
    private Long questionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String studentAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private Integer points;
    private Integer pointsEarned;
}
