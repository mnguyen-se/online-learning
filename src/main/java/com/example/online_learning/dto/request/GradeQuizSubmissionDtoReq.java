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
    @NotNull(message = "Score is required")
    private Integer score;

    private String feedback;

    @NotNull(message = "Answer grades are required")
    private List<AnswerGradeDto> answerGrades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerGradeDto {
        @NotNull(message = "Answer ID is required")
        private Long answerId;

        @NotNull(message = "Points earned is required")
        private Integer pointsEarned;

        private Boolean isCorrect;
    }
}
