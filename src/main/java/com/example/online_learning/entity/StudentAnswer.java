package com.example.online_learning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private AssignmentSubmission submission;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Cho QUIZ: A, B, C, hoặc D. Cho WRITING: text answer (điền vào chỗ trống)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String studentAnswer;
    @Column(nullable = true)
    private Boolean isCorrect;

    @Column(nullable = true)
    private Integer pointsEarned;
}
