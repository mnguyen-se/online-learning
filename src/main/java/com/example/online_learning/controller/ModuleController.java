package com.example.online_learning.controller;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@Tag(
        name = "Module API",
        description = "Quản lý module (chương học) trong khoá học"
)
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    // ================= CREATE MODULE =================
    @Operation(
            summary = "Tạo module mới",
            description = """
                    API dùng để tạo một module (chương học) mới cho khoá học.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo module thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khoá học")
    })
    @PostMapping
    public ResponseEntity<ModuleDtoRes> createModule(
            @Valid @RequestBody ModuleDtoReq dto
    ) {
        ModuleDtoRes result = moduleService.createModule(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // ================= UPDATE MODULE =================
    @Operation(
            summary = "Cập nhật module",
            description = """
                    API dùng để cập nhật thông tin module theo moduleId.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật module thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy module")
    })
    @PutMapping("/{moduleId}")
    public ResponseEntity<ModuleDtoRes> updateModule(
            @Parameter(
                    description = "ID của module",
                    example = "1",
                    required = true
            )
            @PathVariable Long moduleId,

            @Valid @RequestBody ModuleDtoReq dto
    ) {
        ModuleDtoRes result = moduleService.updateModule(moduleId, dto);
        return ResponseEntity.ok(result);
    }

    // ================= DELETE MODULE =================
    @Operation(
            summary = "Xoá module",
            description = """
                    API dùng để xoá một module theo moduleId.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xoá module thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy module")
    })
    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(
            @Parameter(
                    description = "ID của module",
                    example = "1",
                    required = true
            )
            @PathVariable Long moduleId
    ) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    // ================= GET MODULE BY ID =================
    @Operation(
            summary = "Lấy module theo moduleId",
            description = """
                    API trả về thông tin module theo moduleId.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy module thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy module")
    })
    @GetMapping("/{moduleId}")
    public ResponseEntity<List<ModuleDtoRes>> getModuleById(
            @Parameter(
                    description = "ID của module",
                    example = "1",
                    required = true
            )
            @PathVariable Long moduleId
    ) {
        List<ModuleDtoRes> result = moduleService.findModuleById(moduleId);
        return ResponseEntity.ok(result);
    }

    // ================= GET MODULES BY COURSE =================
    @Operation(
            summary = "Lấy danh sách module theo khoá học",
            description = """
                    API trả về danh sách module thuộc một khoá học.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách module thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khoá học")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ModuleDtoRes>> getModulesByCourseId(
            @Parameter(
                    description = "ID của khoá học",
                    example = "10",
                    required = true
            )
            @PathVariable Long courseId
    ) {
        List<ModuleDtoRes> result = moduleService.findModuleByCourseId(courseId);
        return ResponseEntity.ok(result);
    }
}
