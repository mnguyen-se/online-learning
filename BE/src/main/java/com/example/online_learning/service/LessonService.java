package com.example.online_learning.service;

import com.example.online_learning.dto.request.lessonDtoReq;
import com.example.online_learning.dto.response.lessonDtoRes;
import com.example.online_learning.entity.Lesson;

import java.util.List;

public interface LessonService {
    public Lesson createLesson(lessonDtoReq dto);
    public void deleteLesson(Long lessonId);
    public void updateLesson(Long lessonId, lessonDtoReq dto);
    public List<lessonDtoRes> getAllLessons();
    public List<lessonDtoRes> findLessonsBySectionId(Long sectionId);
    public List<lessonDtoRes> findLessonByDeletedFalse();
}
