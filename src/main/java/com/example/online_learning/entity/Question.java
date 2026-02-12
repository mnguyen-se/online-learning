package com.example.online_learning.entity;

import com.example.online_learning.constants.QuestionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    // Cho QUIZ: bắt buộc, cho WRITING: nullable
    @Column(columnDefinition = "TEXT")
    private String optionA;

    @Column(columnDefinition = "TEXT")
    private String optionB;

    @Column(columnDefinition = "TEXT")
    private String optionC;

    @Column(columnDefinition = "TEXT")
    private String optionD;

    // Cho QUIZ: A, B, C, hoặc D. Cho WRITING: có thể là đáp án mẫu hoặc null
    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(nullable = false)
    private Integer orderIndex;

    private Integer points; // Điểm cho câu hỏi (optional)

    // Cho WRITING assignment: loại câu hỏi
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType questionType;

    // Cho WRITING assignment: lưu dữ liệu phức tạp (JSON) cho REORDER, MATCHING, ESSAY_WRITING
    @Column(columnDefinition = "TEXT")
    private String questionData;
}
