package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.lessonDtoReq;
import com.example.online_learning.dto.response.lessonDtoRes;
import com.example.online_learning.entity.CourseSection;
import com.example.online_learning.entity.Lesson;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.LessonRepository;
import com.example.online_learning.repository.SectionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LessonMapper {
    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;

    public LessonMapper(LessonRepository lessonRepository, SectionRepository sectionRepository) {
        this.lessonRepository = lessonRepository;
        this.sectionRepository = sectionRepository;
    }

    public Lesson toEntity(lessonDtoReq lesson){
        Lesson lessonEntity = new Lesson();
        lessonEntity.setLessonType(lesson.getLessonType());
        lessonEntity.setDuration(lesson.getDuration());
        lessonEntity.setTitle(lesson.getTitle());
        lessonEntity.setOrderIndex(lesson.getOrderIndex());
        lessonEntity.setContentUrl(lesson.getContentUrl());
        CourseSection section = sectionRepository.findById(lesson.getSectionId()).orElse(null);
        if(section == null){
            lessonEntity.setSection(null);
            throw new NotFoundException("Section not found!");
        }
        lessonEntity.setSection(section);
        return lessonEntity;
    }

    public lessonDtoRes toDto(Lesson lesson){
        lessonDtoRes dto = new lessonDtoRes();
        dto.setDuration(lesson.getDuration());
        dto.setLessonType(lesson.getLessonType());
        dto.setTitle(lesson.getTitle());
        dto.setOrderIndex(lesson.getOrderIndex());
        dto.setContentUrl(lesson.getContentUrl());
        if(lesson.getSection().getTitle() == null){
            dto.setSectionTitle("No section");
            throw new NotFoundException("Section not found!");
        }
        dto.setSectionTitle(lesson.getSection().getTitle());
        dto.setLessonId(lesson.getLessonId());
        return dto;
    }

    public List<lessonDtoRes> toDto(List<Lesson> lessons){
        List<lessonDtoRes> dtos = new ArrayList<>();
        for(Lesson lesson : lessons) {
            lessonDtoRes dto = toDto(lesson);
            dtos.add(dto);
        }
        return dtos;
    }
}
