package com.example.online_learning.dto.response;

import com.example.online_learning.constants.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingAnswerDetailDtoRes {
    private Long answerId;
    private Long questionId;
    private String questionText;
    private QuestionType questionType;
    private String studentAnswer;
    private Integer points;
    private Integer pointsEarned;
    private Boolean isCorrect;
    private String sampleAnswer;
}
