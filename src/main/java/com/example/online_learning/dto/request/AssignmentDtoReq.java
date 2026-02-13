package com.example.online_learning.dto.request;

import com.example.online_learning.constants.AssignmentType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDtoReq {

    private String title;
    private Long courseId;
    private String description;
    private Integer maxScore;
    private LocalDateTime dueDate;
    private AssignmentType assignmentType; // QUIZ hoặc WRITING, mặc định là QUIZ
}

