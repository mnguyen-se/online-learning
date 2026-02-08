package com.example.online_learning.controller;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(
        name = "Course API",
        description = "Quản lý khoá học (tạo, cập nhật, xem danh sách)"
)
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ================= CREATE COURSE =================
    @Operation(
            summary = "Tạo khoá học mới",
            description = """
                    API dùng để tạo khoá học mới.
                    
                    🔐 Chỉ COURSE_MANAGER hoặc ADMIN mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo khoá học thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<CourseDtoRes> createCourse(
            @RequestBody CourseDtoReq dto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return new ResponseEntity<>(
                courseService.createCourse(dto, userDetail),
                HttpStatus.CREATED
        );
    }

    // ================= UPDATE COURSE =================
    @Operation(
            summary = "Cập nhật khoá học",
            description = """
                    API dùng để cập nhật thông tin khoá học.
                    
                    🔐 Chỉ COURSE_MANAGER hoặc ADMIN mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu cập nhật không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khoá học"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCourse(
            @Parameter(
                    description = "ID của khoá học cần cập nhật",
                    example = "1",
                    required = true
            )
            @PathVariable("id") Long courseId,

            @RequestBody UpdateCourseDtoReq dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        courseService.updateCourse(courseId, dto, userDetail);
        return ResponseEntity.ok().build();
    }

    // ================= GET ALL COURSES =================
    @Operation(
            summary = "Lấy danh sách tất cả khoá học",
            description = """
                    API trả về toàn bộ khoá học (bao gồm cả khoá học đã bị ẩn / xoá mềm).
                    
                    🔐 Chỉ COURSE_MANAGER hoặc ADMIN mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách khoá học thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseDtoRes>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // ================= GET ACTIVE COURSES =================
    @Operation(
            summary = "Lấy danh sách khoá học đang hoạt động",
            description = """
                    API public, trả về các khoá học đang mở (isPublic = true).
                    
                    🌍 Không yêu cầu đăng nhập.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách khoá học thành công"),
            @ApiResponse(responseCode = "404", description = "Không có khoá học nào")
    })
    @GetMapping("/active")
    public ResponseEntity<List<CourseDtoRes>> getActiveCourses() {
        return ResponseEntity.ok(courseService.findCoursesByPublicTrue());
    }
}
