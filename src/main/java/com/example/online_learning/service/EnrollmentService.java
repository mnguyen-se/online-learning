package com.example.online_learning.service;

import com.example.online_learning.dto.request.EnrollStudentDtoReq;
import com.example.online_learning.dto.response.EnrolledCourseDtoRes;
import com.example.online_learning.dto.response.EnrolledStudentDtoRes;
import com.example.online_learning.security.CustomUserDetail;

import java.util.List;

public interface EnrollmentService {
    void enrollStudent(EnrollStudentDtoReq request);
    void unenrollStudent(Long courseId, String username);
    List<EnrolledStudentDtoRes> getEnrolledStudentsByCourseId(Long courseId);
    List<EnrolledCourseDtoRes> getEnrolledCoursesByUsername(String username);
    List<EnrolledCourseDtoRes> getMyEnrolledCourses(CustomUserDetail userDetail);
}
