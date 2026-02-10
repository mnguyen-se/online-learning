package com.example.online_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseFeedbackDtoReq {
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotBlank(message = "Comment is required")
    private String comment;
}
