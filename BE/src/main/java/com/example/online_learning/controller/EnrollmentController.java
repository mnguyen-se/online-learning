package com.example.online_learning.controller;

import com.example.online_learning.dto.request.EnrollStudentDtoReq;
import com.example.online_learning.dto.response.EnrolledCourseDtoRes;
import com.example.online_learning.dto.response.EnrolledStudentDtoRes;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Enrollment API",
        description = "Quản lý ghi danh học viên vào khoá học"
)
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // ================= ENROLL STUDENT =================
    @Operation(
            summary = "Ghi danh học viên vào khoá học",
            description = """
                    API dùng để ghi danh một học viên vào khoá học.
                    
                    🔐 Chỉ ADMIN hoặc COURSE_MANAGER được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ghi danh thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy học viên hoặc khoá học"),
            @ApiResponse(responseCode = "409", description = "Học viên đã được ghi danh trước đó"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('ADMIN','COURSE_MANAGER')")
    @PostMapping
    public ResponseEntity<Void> enrollStudent(
            @Valid @RequestBody EnrollStudentDtoReq request
    ) {
        enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ================= UNENROLL STUDENT =================
    @Operation(
            summary = "Huỷ ghi danh học viên khỏi khoá học",
            description = """
                    API dùng để huỷ ghi danh một học viên khỏi khoá học.
                    
                    🔐 Chỉ ADMIN hoặc COURSE_MANAGER được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Huỷ ghi danh thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thông tin ghi danh"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('ADMIN','COURSE_MANAGER')")
    @DeleteMapping("/courses/{courseId}/students/{username}")
    public ResponseEntity<Void> unenrollStudent(
            @Parameter(
                    description = "ID của khoá học",
                    example = "1",
                    required = true
            )
            @PathVariable Long courseId,

            @Parameter(
                    description = "Username của học viên",
                    example = "student01",
                    required = true
            )
            @PathVariable String username
    ) {
        enrollmentService.unenrollStudent(courseId, username);
        return ResponseEntity.ok().build();
    }

    // ================= GET STUDENTS BY COURSE =================
    @Operation(
            summary = "Lấy danh sách học viên của khoá học",
            description = """
                    API trả về danh sách học viên đã ghi danh vào một khoá học.
                    
                    🔐 Chỉ ADMIN hoặc COURSE_MANAGER được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách học viên thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khoá học"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('ADMIN','COURSE_MANAGER')")
    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<EnrolledStudentDtoRes>> getEnrolledStudents(
            @Parameter(
                    description = "ID của khoá học",
                    example = "1",
                    required = true
            )
            @PathVariable Long courseId
    ) {
        List<EnrolledStudentDtoRes> students =
                enrollmentService.getEnrolledStudentsByCourseId(courseId);
        return ResponseEntity.ok(students);
    }

    // ================= GET COURSES BY USERNAME =================
    @Operation(
            summary = "Lấy danh sách khoá học của học viên (theo username)",
            description = """
                    API trả về các khoá học mà một học viên đã ghi danh.
                    
                    🔐 Không yêu cầu đăng nhập (tuỳ logic hệ thống).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách khoá học thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy học viên")
    })
    @GetMapping("/students/{username}/courses")
    public ResponseEntity<List<EnrolledCourseDtoRes>> getEnrolledCourses(
            @Parameter(
                    description = "Username của học viên",
                    example = "student01",
                    required = true
            )
            @PathVariable String username
    ) {
        List<EnrolledCourseDtoRes> courses =
                enrollmentService.getEnrolledCoursesByUsername(username);
        return ResponseEntity.ok(courses);
    }

    // ================= GET MY COURSES =================
    @Operation(
            summary = "Lấy danh sách khoá học của học viên đang đăng nhập",
            description = """
                    API trả về các khoá học mà học viên hiện tại đã ghi danh.
                    
                    🔐 Chỉ role STUDENT được phép.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách khoá học thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<EnrolledCourseDtoRes>> getMyEnrolledCourses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        List<EnrolledCourseDtoRes> courses =
                enrollmentService.getMyEnrolledCourses(userDetail);
        return ResponseEntity.ok(courses);
    }
}
