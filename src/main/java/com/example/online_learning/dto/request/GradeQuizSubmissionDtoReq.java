package com.example.online_learning.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeQuizSubmissionDtoReq {
    private Integer score;

    private String feedback;

    private List<AnswerGradeDto> answerGrades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerGradeDto {
        @NotNull(message = "Answer ID is required")
        private Long answerId;

        private Integer pointsEarned;

        private Boolean isCorrect;
    }
}
