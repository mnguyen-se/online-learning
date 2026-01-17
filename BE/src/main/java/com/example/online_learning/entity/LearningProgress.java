package com.example.online_learning.entity;

import com.example.online_learning.constants.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "learning_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "total_tasks", nullable = false)
    private int totalTasks;

    @Column(name = "completed_tasks", nullable = false)
    private int completedTasks;

    @Enumerated(EnumType.STRING)
    private ProgressStatus status;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}

