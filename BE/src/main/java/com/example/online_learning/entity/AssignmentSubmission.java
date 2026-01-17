package com.example.online_learning.entity;

import com.example.online_learning.constants.SubmissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "user_id"}))
@Getter
@Setter
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @Column(columnDefinition = "TEXT")
    private String content; // link file hoặc text

    private LocalDateTime submittedAt;

    private Integer score;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;
}

