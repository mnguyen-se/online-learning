package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.entity.Course;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseMapper {
    private final ModuleMapper moduleMapper;

    public CourseMapper(ModuleMapper moduleMapper) {
        this.moduleMapper = moduleMapper;
    }

    public Course toEntity(CourseDtoReq dto){
        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setIsPublic(false);
        return course;
    }

    public CourseDtoRes toDto(Course course){
        CourseDtoRes dto = new CourseDtoRes();
        dto.setCourseId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setPublic(course.getIsPublic());
        dto.setCreatedBy(course.getCreatedBy());
        dto.setModules(new ArrayList<>());
        dto.setModules(moduleMapper.toDto(course.getModules()));
        return dto;
    }

    public List<CourseDtoRes> toDto(List<Course> courses){
        List<CourseDtoRes> dtos = new ArrayList<>();
        for(Course course : courses) {
            CourseDtoRes dto = toDto(course);
            dtos.add(dto);
        }
        return dtos;
    }
}
