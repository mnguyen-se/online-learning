package com.example.online_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollStudentDtoReq {
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
