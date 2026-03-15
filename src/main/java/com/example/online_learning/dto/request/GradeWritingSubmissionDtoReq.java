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
public class GradeWritingSubmissionDtoReq {
    private Integer score;

    private String feedback; // Feedback từ giáo viên (optional)

    @NotNull(message = "Answer grades are required")
    private List<AnswerGradeDto> answerGrades; // Chấm điểm từng câu

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerGradeDto {
        @NotNull(message = "Answer ID is required")
        private Long answerId;

        @NotNull(message = "Points earned is required")
        private Integer pointsEarned; // Điểm đạt được cho câu này

        private Boolean isCorrect; // Đúng hoặc sai (optional)
    }
}
