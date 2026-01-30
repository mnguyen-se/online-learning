package com.example.online_learning.dto.response;

import com.example.online_learning.entity.Course;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AssignmentDtoRes {
    private Long courseId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer maxScore;

    private LocalDateTime dueDate;

    private Integer orderIndex;
}
