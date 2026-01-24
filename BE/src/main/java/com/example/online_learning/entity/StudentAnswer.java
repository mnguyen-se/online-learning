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

    @Column(nullable = false, length = 1)
    private String studentAnswer;

    @Column(nullable = false)
    private Boolean isCorrect;

    private Integer pointsEarned;
}
