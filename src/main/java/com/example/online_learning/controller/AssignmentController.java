package com.example.online_learning.controller;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignment Core", description = "Quản lý assignment chung")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(
            AssignmentService assignmentService
    ) {
        this.assignmentService = assignmentService;
    }

    @Operation(
            summary = "Tạo assignment",
            description = "COURSE_MANAGER / ADMIN tạo assignment cho một khóa học"
    )
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/courses/assignments")
    public ResponseEntity<?> createAssignment(
             @RequestBody AssignmentDtoReq request
    ) {
        return ResponseEntity.ok(
                assignmentService.createAssignment(request)
        );
    }

    @Operation(
            summary = "Xem assignment",
            description = "Lấy thông tin assignment theo assignmentId"
    )
    @GetMapping("/get/{assignmentId}")
    public ResponseEntity<?> viewAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(
                assignmentService.viewAssignment(assignmentId)
        );
    }

    @Operation(
            summary = "Lấy assignment theo course",
            description = "Lấy danh sách assignment thuộc một khóa học"
    )
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> viewAssignmentByCourseId(
            @PathVariable long courseId
    ) {
        return ResponseEntity.ok(
                assignmentService.findByCourseId(courseId)
        );
    }

    @Operation(
            summary = "Lấy assignment của học sinh đã enroll",
            description = "Học sinh xem danh sách assignment thuộc các khóa học đã enroll"
    )
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @GetMapping("/my-assignments")
    public ResponseEntity<?> getMyAssignments(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(
                assignmentService.findMyAssignments(userDetail.getUser().getUserId())
        );
    }
}
