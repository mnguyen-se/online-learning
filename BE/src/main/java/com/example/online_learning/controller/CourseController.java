package com.example.online_learning.controller;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ✅ Create course
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseDtoReq dto,
                                             @AuthenticationPrincipal CustomUserDetail userDetail) {
        return new ResponseEntity<>(courseService.createCourse(dto, userDetail), HttpStatus.CREATED);
    }

    // ✅ Update course
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCourse(
            @PathVariable("id") Long courseId,
            @RequestBody CourseDtoReq dto,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        courseService.updateCourse(courseId, dto, userDetail);
        return ResponseEntity.ok().build();
    }

    // ✅ Get all courses (kể cả deleted)
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // ✅ Get only active courses (deleted = false)
    @GetMapping("/active")
    public ResponseEntity<List<CourseDtoRes>> getActiveCourses() {
        return ResponseEntity.ok(courseService.findCoursesByPublicTrue());
    }
}

