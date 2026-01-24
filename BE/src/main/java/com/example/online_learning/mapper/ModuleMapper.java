package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Lesson;
import com.example.online_learning.entity.Module;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.LessonRepository;
import com.example.online_learning.repository.ModuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModuleMapper {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;
    public ModuleMapper(ModuleRepository moduleRepository, CourseRepository courseRepository, LessonMapper lessonMapper) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
        this.lessonMapper = lessonMapper;
    }
    public Module toEntity(ModuleDtoReq dto){
        Module module = new Module();
        module.setTitle(dto.getTitle());
        Course course = courseRepository.findByCourseId(dto.getCourseId());
        if(course == null) throw new NotFoundException("Course not found");
        module.setCourse(course);
        module.setOrderIndex(dto.getOrderIndex());
        module.setIsPublic(dto.getIsPublic());
        return module;
    }

    public ModuleDtoRes toDto(Module module) {
        ModuleDtoRes dto = new ModuleDtoRes();
        dto.setTitle(module.getTitle());
        dto.setModuleId(module.getModuleId());
        dto.setCourseId(module.getCourse().getCourseId());
        dto.setOrderIndex(module.getOrderIndex());

        dto.setLessons(
                module.getLessons() == null
                        ? List.of()
                        : lessonMapper.toDto(module.getLessons())
        );

        return dto;
    }


    public List<ModuleDtoRes> toDto(List<Module> modules){
        List<ModuleDtoRes> dtos = new java.util.ArrayList<>();
        for(Module module : modules) {
            ModuleDtoRes dto = toDto(module);
            dtos.add(dto);
        }
        return dtos;
    }

    public ModuleDtoRes updateModule(ModuleDtoReq dto, Module module){
        module.setTitle(dto.getTitle());
        module.setCourse(module.getCourse());
        module.setOrderIndex(module.getOrderIndex());
        module.setIsPublic(dto.getIsPublic());
        module = moduleRepository.save(module);
        return this.toDto(module);
    }
}
