package com.example.online_learning.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseGetAllDtoRes {
    private Long courseId;
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long createdById;

    private Long teacherId;

    private LocalDateTime createdAt;

    private boolean isPublic;

    private Long moduleCount;

    private Long assignmentCount;
}
