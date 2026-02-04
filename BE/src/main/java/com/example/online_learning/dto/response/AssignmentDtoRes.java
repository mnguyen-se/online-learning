package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDtoRes {
    private Long assignmentId;
    private Long courseId;
    private String title;
    private String description;

    private Integer maxScore;

    private LocalDateTime dueDate;

    private Integer orderIndex;
}
