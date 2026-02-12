package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.dto.response.CourseGetAllDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.ModuleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {
    private final ModuleMapper moduleMapper;
    private final AssignmentRepository assignmentRepository;
    private final ModuleRepository moduleRepository;

    public CourseMapper(ModuleMapper moduleMapper, AssignmentRepository assignmentRepository, ModuleRepository moduleRepository) {
        this.moduleMapper = moduleMapper;
        this.assignmentRepository = assignmentRepository;
        this.moduleRepository = moduleRepository;
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
        dto.setCreatedById(course.getCreatedBy().getUserId());
        dto.setTeacherId(
                course.getTeacher() != null
                        ? course.getTeacher().getUserId()
                        : null
        );

        dto.setModules(new ArrayList<>());
        dto.setModules(moduleMapper.toDto(course.getModules()));
        
        // Lấy danh sách assignment IDs của course
        List<Long> assignmentIds = assignmentRepository.findByCourse_CourseId(course.getCourseId())
                .stream()
                .map(assignment -> assignment.getAssignmentId())
                .collect(Collectors.toList());
        dto.setAssignmentIds(assignmentIds);
        
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

    public CourseGetAllDtoRes toAll(Course course){
        CourseGetAllDtoRes dto = new CourseGetAllDtoRes();
        dto.setCourseId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setPublic(course.getIsPublic());
        dto.setCreatedById(course.getCreatedBy().getUserId());
        dto.setTeacherId(course.getTeacher() != null ? course.getTeacher().getUserId() : null);
        dto.setAssignmentCount(assignmentRepository.countByCourse_CourseId(course.getCourseId()));
        dto.setModuleCount(moduleRepository.countByCourse_CourseId(course.getCourseId()));
        return dto;
    }
}
