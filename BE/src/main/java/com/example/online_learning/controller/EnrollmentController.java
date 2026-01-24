package com.example.online_learning.controller;

import com.example.online_learning.dto.request.EnrollStudentDtoReq;
import com.example.online_learning.dto.response.EnrolledCourseDtoRes;
import com.example.online_learning.dto.response.EnrolledStudentDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

 
    @PreAuthorize("hasAnyRole('ADMIN','COURSE_MANAGER')")
    @PostMapping
    public ResponseEntity<Void> enrollStudent(@Valid @RequestBody EnrollStudentDtoReq request) {
        enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','COURSE_MANAGER')")
    @DeleteMapping("/courses/{courseId}/students/{username}")
    public ResponseEntity<Void> unenrollStudent(
            @PathVariable Long courseId,
            @PathVariable String username
    ) {
        enrollmentService.unenrollStudent(courseId, username);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','COURSE_MANAGER')")
    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<EnrolledStudentDtoRes>> getEnrolledStudents(
            @PathVariable Long courseId
    ) {
        List<EnrolledStudentDtoRes> students = enrollmentService.getEnrolledStudentsByCourseId(courseId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{username}/courses")
    public ResponseEntity<List<EnrolledCourseDtoRes>> getEnrolledCourses(
            @PathVariable String username
    ) {
        List<EnrolledCourseDtoRes> courses = enrollmentService.getEnrolledCoursesByUsername(username);
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<EnrolledCourseDtoRes>> getMyEnrolledCourses(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        List<EnrolledCourseDtoRes> courses = enrollmentService.getMyEnrolledCourses(userDetail);
        return ResponseEntity.ok(courses);
    }
}
