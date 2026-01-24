package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.dto.response.LessonDtoRes;
import com.example.online_learning.entity.Lesson;
import com.example.online_learning.entity.Module;
import com.example.online_learning.repository.ModuleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LessonMapper {
    private final ModuleRepository moduleRepository;

    public LessonMapper(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Lesson toEntity(LessonDtoReq lesson){
        Lesson lessonEntity = new Lesson();
        lessonEntity.setLessonType(lesson.getLessonType());
        lessonEntity.setTitle(lesson.getTitle());
        lessonEntity.setContentUrl(lesson.getContentUrl());
        Module module = moduleRepository.getReferenceById(lesson.getModuleId());
        lessonEntity.setModule(module);
        return lessonEntity;
    }

    public LessonDtoRes toDto(Lesson lesson){
        LessonDtoRes dto = new LessonDtoRes();
        dto.setLessonType(lesson.getLessonType());
        dto.setTitle(lesson.getTitle());
        dto.setOrderIndex(lesson.getOrderIndex());
        dto.setContentUrl(lesson.getContentUrl());
        dto.setLessonId(lesson.getLessonId());
        dto.setModuleId(lesson.getModule().getModuleId());
        return dto;
    }

    public List<LessonDtoRes> toDto(List<Lesson> lessons){
        List<LessonDtoRes> dtos = new ArrayList<>();
        for(Lesson lesson : lessons) {
            LessonDtoRes dto = toDto(lesson);
            dtos.add(dto);
        }
        return dtos;
    }
}
