package com.example.online_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class SubmitAnswersDtoReq {
    @NotNull(message = "Answers list is required")
    private List<AnswerDto> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDto {
        @NotNull(message = "Question ID is required")
        private Long questionId;

        @NotBlank(message = "Answer is required")
        private String answer;
    }
}
