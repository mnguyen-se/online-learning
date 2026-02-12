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
public class SubmitWritingAnswersDtoReq {
    @NotNull(message = "Answers list is required")
    private List<AnswerDto> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDto {
        @NotNull(message = "Question ID is required")
        private Long questionId;

        // Cho FILL_BLANK và ESSAY_WRITING: text answer
        private String answer;

        // Cho REORDER: thứ tự sắp xếp
        private List<String> orderedItems;

        // Cho MATCHING: danh sách các cặp nối
        private List<MatchingPairDto> matchingPairs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingPairDto {
        private String aId; // ID của item ở cột A
        private String bId; // ID của item ở cột B
    }
}
