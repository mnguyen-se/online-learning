package com.example.online_learning.dto.response;

import com.example.online_learning.constants.AssignmentType;
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
    
    private AssignmentType assignmentType; // QUIZ hoặc WRITING
}
