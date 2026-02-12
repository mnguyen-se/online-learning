package com.example.online_learning.dto.request;

import com.example.online_learning.constants.QuestionType;
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
public class WritingQuestionDtoReq {
    @NotBlank(message = "Question text is required")
    private String questionText; // Ví dụ: "わたし（　）学生です。"

    @NotNull(message = "Question type is required")
    private QuestionType questionType; // FILL_BLANK, ESSAY_WRITING, REORDER, MATCHING

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    private Integer points; // Điểm cho câu hỏi (optional)
    
    private String sampleAnswer; // Đáp án mẫu (optional, để giáo viên tham khảo)

    // Cho ESSAY_WRITING: yêu cầu về độ dài, chủ đề, hướng dẫn
    private Integer minWords; // Số từ tối thiểu (ví dụ: 100)
    private Integer maxWords; // Số từ tối đa (ví dụ: 150)
    private String topic; // Chủ đề bài viết
    private String instructions; // Hướng dẫn chi tiết

    // Cho REORDER: danh sách các từ/câu cần sắp xếp
    private List<String> items;

    // Cho MATCHING: danh sách cột A và B
    private List<MatchingItemDto> columnA;
    private List<MatchingItemDto> columnB;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingItemDto {
        private String id;
        private String text;
    }
}
