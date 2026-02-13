package com.example.online_learning.dto.response;

import com.example.online_learning.constants.QuestionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDtoRes {
    private Long questionId;
    private Long assignmentId;
    private String questionText;
    private QuestionType questionType; // FILL_BLANK, ESSAY_WRITING, REORDER, MATCHING
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer; // A, B, C, hoặc D (cho QUIZ) / Đáp án mẫu (cho WRITING)
    private Integer orderIndex;
    private Integer points;

  
    private List<String> items; 
    private List<MatchingItemDto> columnA; 
    private List<MatchingItemDto> columnB; 

    // Cho ESSAY_WRITING
    private String topic;
    private Integer minWords;
    private Integer maxWords;
    private String instructions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingItemDto {
        private String id;
        private String text;
    }
}
