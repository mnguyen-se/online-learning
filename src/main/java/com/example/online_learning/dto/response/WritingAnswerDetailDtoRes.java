package com.example.online_learning.dto.response;

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
    private String studentAnswer; // Đáp án học sinh điền
    private Integer points; // Điểm tối đa của câu
    private Integer pointsEarned; // Điểm đạt được (null nếu chưa chấm)
    private Boolean isCorrect; // Đúng/sai (null nếu chưa chấm)
    private String sampleAnswer; // Đáp án mẫu (nếu có)
}
