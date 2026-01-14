package com.example.online_learning.controller;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.service.ModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleController {
    private final ModuleService moduleService;
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllModules(){
        return ResponseEntity.ok().body(moduleService.getAllModules());
    }

    @GetMapping("/view")
    public ResponseEntity<?> getAllModulesDeletedFalse(){
        return ResponseEntity.ok().body(moduleService.findModulesByDeletedFalse());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createModule(@RequestBody ModuleDtoReq dto){
        moduleService.createModule(dto);
        return ResponseEntity.ok().body("module created");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteModule(Long moduleId){
        moduleService.deleteModule(moduleId);
        return ResponseEntity.ok().body("module deleted");
    }

    @GetMapping("/title")
    public ResponseEntity<?> findModuleByTitle(String title){
        return ResponseEntity.ok().body(moduleService.findModuleByTitle(title));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> findModulesByCourseId(@PathVariable Long courseId){
        return ResponseEntity.ok().body(moduleService.findModulesByCourseId(courseId));
    }

    @PutMapping("/update/{moduleId}")
    public ResponseEntity<?> updateModule(@PathVariable Long moduleId,@RequestBody ModuleDtoReq dto){
        moduleService.updateModule(moduleId, dto);
        return ResponseEntity.ok("module updated");
    }

}
