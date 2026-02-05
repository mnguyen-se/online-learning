package com.example.online_learning.controller;

import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.service.LessonService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/lessons")
public class LessonController {
    private final LessonService lessonService;
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @GetMapping("/")
    public Object getAllLessons(){
        return lessonService.getAllLessons();
    }

    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@RequestBody LessonDtoReq dto){
        return new ResponseEntity<>(lessonService.createLesson(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @PutMapping("/update/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long lessonId, @RequestBody LessonDtoReq dto){
        lessonService.updateLesson(lessonId, dto);
        return ResponseEntity.ok("Lesson updated");
    }

    @PreAuthorize("hasAnyRole('COURSE_MANAGER','ADMIN')")
    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId){
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok().body("delete lesson");
    }

    @GetMapping("/view")
    public ResponseEntity<?> getAllLessonsIsNotDeleted(){
        return ResponseEntity.ok().body(lessonService.findLessonByPublicTrue());
    }

    @PostMapping(
            value = "/lessons/{lessonId}/upload-video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadVideo(
            @PathVariable Long lessonId,
            @RequestPart("file") MultipartFile file){
        String videoUrl = lessonService.uploadFile(lessonId, file);
        return ResponseEntity.ok(videoUrl);
    }


}
