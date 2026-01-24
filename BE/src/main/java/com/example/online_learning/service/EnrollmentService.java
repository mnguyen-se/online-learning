package com.example.online_learning.service;

import com.example.online_learning.dto.request.EnrollStudentDtoReq;

public interface EnrollmentService {
    void enrollStudent(EnrollStudentDtoReq request);
    void unenrollStudent(Long courseId, String username);
}
