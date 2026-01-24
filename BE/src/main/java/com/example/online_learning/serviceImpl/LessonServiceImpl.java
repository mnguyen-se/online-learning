package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.dto.response.LessonDtoRes;
import com.example.online_learning.entity.Lesson;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.LessonMapper;
import com.example.online_learning.repository.LessonRepository;
import com.example.online_learning.service.LessonService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    public LessonServiceImpl(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }
    @Override
    public LessonDtoRes createLesson(LessonDtoReq dto) {
        Lesson l = lessonMapper.toEntity(dto);
        l.setOrderIndex(lessonRepository.findMaxOrderIndexByCourseId(dto.getModuleId()) + 1);
        lessonRepository.save(l);
        return lessonMapper.toDto(l);
    }

    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        lessonRepository.delete(lesson);
    }

    @Override
    public void updateLesson(Long lessonId, LessonDtoReq dto) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        lesson.setLessonType(dto.getLessonType());
        lesson.setTitle(dto.getTitle());
        lesson.setContentUrl(dto.getContentUrl());
        lessonRepository.save(lesson);
    }

    @Override
    public List<LessonDtoRes> getAllLessons() {
        return lessonMapper.toDto(lessonRepository.findAll());
    }

    @Override
    public List<LessonDtoRes> findLessonByPublicTrue() {
        List<Lesson> lessons = lessonRepository.findAllByIsPublicTrue();
        if(lessons.isEmpty()) throw new NotFoundException("No lesson found");
        return lessonMapper.toDto(lessons);
    }


}
