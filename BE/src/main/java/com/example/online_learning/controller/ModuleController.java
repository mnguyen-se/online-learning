package com.example.online_learning.controller;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    // ✅ Create module
    @PostMapping
    public ResponseEntity<ModuleDtoRes> createModule(
            @Valid @RequestBody ModuleDtoReq dto
    ) {
        ModuleDtoRes result = moduleService.createModule(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // ✅ Update module
    @PutMapping("/{moduleId}")
    public ResponseEntity<ModuleDtoRes> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleDtoReq dto
    ) {
        ModuleDtoRes result = moduleService.updateModule(moduleId, dto);
        return ResponseEntity.ok(result);
    }

    // ✅ Delete module
    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(
            @PathVariable Long moduleId
    ) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get module by moduleId
    @GetMapping("/{moduleId}")
    public ResponseEntity<List<ModuleDtoRes>> getModuleById(
            @PathVariable Long moduleId
    ) {
        List<ModuleDtoRes> result = moduleService.findModuleById(moduleId);
        return ResponseEntity.ok(result);
    }

    // ✅ Get modules by courseId
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ModuleDtoRes>> getModulesByCourseId(
            @PathVariable Long courseId
    ) {
        List<ModuleDtoRes> result = moduleService.findModuleByCourseId(courseId);
        return ResponseEntity.ok(result);
    }
}

