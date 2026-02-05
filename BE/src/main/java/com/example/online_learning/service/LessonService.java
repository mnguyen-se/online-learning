package com.example.online_learning.service;

import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.dto.response.LessonDtoRes;
import com.example.online_learning.entity.Lesson;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LessonService {
    public LessonDtoRes createLesson(LessonDtoReq dto);
    public void deleteLesson(Long lessonId);
    public void updateLesson(Long lessonId, LessonDtoReq dto);
    public List<LessonDtoRes> getAllLessons();
    public List<LessonDtoRes> findLessonByPublicTrue();
    public String uploadFile(Long lessonId, MultipartFile file);
}
