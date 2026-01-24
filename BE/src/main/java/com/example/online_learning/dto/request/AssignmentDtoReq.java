package com.example.online_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDtoReq {

    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private Integer maxScore;
    private Integer dueDate;
    private Integer orderIndex;
}

