package com.example.online_learning.controller;

import com.example.online_learning.dto.request.EnrollStudentDtoReq;
import com.example.online_learning.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
