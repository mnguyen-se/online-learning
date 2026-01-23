package com.example.online_learning.service;

import com.example.online_learning.dto.request.lessonDtoReq;
import com.example.online_learning.dto.response.LessonDtoRes;
import com.example.online_learning.entity.Lesson;

import java.util.List;

public interface LessonService {
    public LessonDtoRes createLesson(lessonDtoReq dto);
    public void deleteLesson(Long lessonId);
    public void updateLesson(Long lessonId, lessonDtoReq dto);
    public List<Lesson> getAllLessons();
    public List<LessonDtoRes> findLessonByPublicTrue();
}
