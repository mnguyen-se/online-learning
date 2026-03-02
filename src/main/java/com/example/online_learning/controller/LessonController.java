package com.example.online_learning.controller;

import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "Lesson API",
        description = "Các API quản lý bài học (Lesson)"
)
@RestController
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    // ==================== GET ALL LESSONS ====================
    @Operation(
            summary = "Lấy danh sách tất cả bài học",
            description = "API dành cho COURSE_MANAGER hoặc ADMIN",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @GetMapping("/")
    public Object getAllLessons() {
        return lessonService.getAllLessons();
    }

    // ==================== CREATE LESSON ====================
    @Operation(
            summary = "Tạo bài học mới",
            description = "Tạo lesson mới với thông tin truyền vào",
            security = @SecurityRequirement(name = "bearerAuth", scopes = {})
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo bài học thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLesson(
            @Parameter(description = "Thông tin bài học")
            @RequestBody LessonDtoReq dto
    ) {
        return new ResponseEntity<>(lessonService.createLesson(dto), HttpStatus.CREATED);
    }

    // ==================== UPDATE LESSON ====================
    @Operation(
            summary = "Cập nhật bài học",
            description = "Cập nhật thông tin bài học theo ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PutMapping("/update/{lessonId}")
    public ResponseEntity<?> updateLesson(
            @Parameter(description = "ID bài học", example = "1")
            @PathVariable Long lessonId,

            @Parameter(description = "Thông tin cập nhật bài học")
            @RequestBody LessonDtoReq dto
    ) {
        lessonService.updateLesson(lessonId, dto);
        return ResponseEntity.ok("Lesson updated");
    }

    // ==================== DELETE LESSON ====================
    @Operation(
            summary = "Xoá bài học",
            description = "Xoá mềm (soft delete) bài học theo ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xoá thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<?> deleteLesson(
            @Parameter(description = "ID bài học", example = "1")
            @PathVariable Long lessonId
    ) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok("Delete lesson success");
    }

    // ==================== VIEW PUBLIC LESSONS ====================
    @Operation(
            summary = "Xem danh sách bài học public",
            description = "Lấy danh sách bài học chưa bị xoá và được public"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy dữ liệu thành công")
    })
    @GetMapping("/view")
    public ResponseEntity<?> getAllLessonsIsNotDeleted() {
        return ResponseEntity.ok(lessonService.findLessonByPublicTrue());
    }

    // ==================== UPLOAD VIDEO ====================
    @Operation(
            summary = "Upload video cho bài học",
            description = "Upload video MP4 cho lesson (giới hạn 3 phút, 480p)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upload thành công"),
            @ApiResponse(responseCode = "400", description = "File không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học")
    })
    @PostMapping(
            value = "/{lessonId}/upload-video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadVideo(
            @Parameter(description = "ID bài học", example = "1")
            @PathVariable Long lessonId,

            @Parameter(
                    description = "File video MP4",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("file") MultipartFile file
    ) {
        String videoUrl = lessonService.uploadFile(lessonId, file);
        return ResponseEntity.ok(videoUrl);
    }

    @GetMapping("/IdAndPublic")
    public ResponseEntity<?> getLessonByModuleIdAndIsPublicTrue(@RequestParam("moduleId") Long moduleId) {
        return ResponseEntity.ok().body(lessonService.getLessonsByModuleIdAndIsPublicTrue(moduleId));
    }
}
