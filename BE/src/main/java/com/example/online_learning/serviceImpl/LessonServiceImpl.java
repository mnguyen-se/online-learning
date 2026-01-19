package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.lessonDtoReq;
import com.example.online_learning.dto.response.lessonDtoRes;
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
    public Lesson createLesson(lessonDtoReq dto) {
        return lessonRepository.save(lessonMapper.toEntity(dto));
    }

    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        lesson.setDeleted(true);
        lessonRepository.save(lesson);
    }

    @Override
    public void updateLesson(Long lessonId, lessonDtoReq dto) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        lesson.setLessonType(dto.getLessonType());
        lesson.setDuration(dto.getDuration());
        lesson.setTitle(dto.getTitle());
        lesson.setOrderIndex(dto.getOrderIndex());
        lesson.setContentUrl(dto.getContentUrl());

        lessonRepository.save(lesson);
    }

    @Override
    public List<lessonDtoRes> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAllByDeletedFalse();
        return lessonMapper.toDto(lessons);
    }

    @Override
    public List<lessonDtoRes> findLessonByDeletedFalse() {
        List<Lesson> lessons = lessonRepository.findAllByDeletedFalse();
        if(lessons.isEmpty()) throw new NotFoundException("No lesson found");
        return lessonMapper.toDto(lessons);
    }


}
