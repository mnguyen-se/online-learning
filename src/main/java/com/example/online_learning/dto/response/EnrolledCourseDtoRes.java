package com.example.online_learning.dto.response;

import com.example.online_learning.constants.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledCourseDtoRes {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private Boolean isPublic;
    private LocalDateTime courseCreatedAt;
    private EnrollmentStatus enrollmentStatus;
    private LocalDateTime enrolledAt;
    private Long teacherId;
    private String teacherName;
}
