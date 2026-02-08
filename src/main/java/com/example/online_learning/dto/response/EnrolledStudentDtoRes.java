package com.example.online_learning.dto.response;

import com.example.online_learning.constants.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledStudentDtoRes {
    private Long enrollmentId;
    private Long studentId;
    private String username;
    private String name;
    private String email;
    private String address;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}
