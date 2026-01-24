package com.example.online_learning.entity;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionA;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionB;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionC;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionD;

    @Column(nullable = false, length = 1)
    private String correctAnswer; // A, B, C, hoặc D

    @Column(nullable = false)
    private Integer orderIndex;

    private Integer points; // Điểm cho câu hỏi (optional)
}
