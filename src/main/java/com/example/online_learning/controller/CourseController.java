package com.example.online_learning.controller;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import com.example.online_learning.service.UserService;
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
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
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

    // ================= GET ALL TEACHERS =================
    @Operation(
            summary = "Lấy danh sách giáo viên",
            description = """
                    API dùng để lấy danh sách tất cả giáo viên (users có role TEACHER).
                    
                    🔐 Chỉ COURSE_MANAGER hoặc ADMIN mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách giáo viên thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @GetMapping("/teachers")
    public ResponseEntity<List<UserDtoRes>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    }

    @Operation(
            summary = "Lấy danh sách khóa học của giáo viên",
            description = """
                    API dùng để Teacher xem các khóa học mà mình đang quản lý.
                    
                    🔐 Chỉ TEACHER mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách khóa học thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseDtoRes>> getMyCourses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ResponseEntity.ok(courseService.getMyCourses(userDetail));
    }

    @Operation(
            summary = "Lấy thông tin khóa học theo ID",
            description = "API trả về thông tin khóa học theo ID nếu khóa học ở trạng thái public. "
                    + "Nếu khóa học không tồn tại hoặc không public thì sẽ trả về lỗi."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin khóa học thành công"),
            @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khóa học hoặc khóa học không public")
    })
    @GetMapping("/getById")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID của khóa học", example = "1", required = true)
            @RequestParam("id") Long id) {

        return ResponseEntity.ok(courseService.getByIdAndIsPublicTrue(id));
    }

    @Operation(
            summary = "Xóa vĩnh viễn khóa học không hoạt động",
            description = """
                    API dùng để xóa vĩnh viễn khóa học có isPublic = false.
                    Chỉ cho phép xóa các khóa học không hoạt động.
                    
                    ⚠️ Hành động này không thể hoàn tác!
                    
                    🔐 Chỉ COURSE_MANAGER hoặc ADMIN mới được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa khóa học thành công"),
            @ApiResponse(responseCode = "400", description = "Không thể xóa khóa học đang hoạt động"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khóa học"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> deleteInactiveCoursePermanently(
            @Parameter(
                    description = "ID của khóa học cần xóa",
                    example = "1",
                    required = true
            )
            @PathVariable("id") Long courseId
    ) {
        courseService.deleteInactiveCoursePermanently(courseId);
        return ResponseEntity.ok().build();
    }
}
