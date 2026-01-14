package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.CourseModule;
import com.example.online_learning.repository.CourseRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModuleMapper {
    private final CourseRepository courseRepository;
    public ModuleMapper(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    public CourseModule toEntity(ModuleDtoReq dto){
        CourseModule module = new CourseModule();
        module.setTitle(dto.getTitle());
        module.setOrderIndex(dto.getOrderIndex());
        module.setDeleted(false);
        Course course = courseRepository.findByCourseId(dto.getCourseId());
        module.setCourse(course);
        return module;
    }

    public ModuleDtoRes toDto(CourseModule module){
        ModuleDtoRes dto = new ModuleDtoRes();
        dto.setTitle(module.getTitle());
        dto.setOrderIndex(module.getOrderIndex());
        dto.setCourseTitle(module.getCourse().getTitle());
        dto.setOrderIndex(module.getOrderIndex());
        return dto;
    }

    public List<ModuleDtoRes> toDto(List<CourseModule> modules){
        List<ModuleDtoRes> dtos = new ArrayList<>();
        for(CourseModule module : modules) {
            ModuleDtoRes dto = toDto(module);
            dtos.add(dto);
        }
        return dtos;
    }
}
