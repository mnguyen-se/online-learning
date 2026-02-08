package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.CourseMapper;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }
    @Override
    public CourseDtoRes createCourse(CourseDtoReq dto, CustomUserDetail userDetail) {
        Course course = courseMapper.toEntity(dto);
        course.setCreatedBy(userDetail.getUser());
        course.setIsPublic(false);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Override
    public CourseDtoRes updateCourse(Long courseId, UpdateCourseDtoReq dto, CustomUserDetail userDetail) {
        Course course = courseRepository.findByCourseId(courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        // update từng field, bỏ qua field null
        if (dto.getTitle() != null) {
            course.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        course.setIsPublic(dto.isPublic());

        // audit
        course.setCreatedBy(userDetail.getUser());
        course.setCreatedAt(LocalDateTime.now());

        courseRepository.save(course);
        return courseMapper.toDto(course);
    }


    @Override
    public List<CourseDtoRes> getAllCourses() {
        return courseMapper.toDto(courseRepository.findAll());
    }

    @Override
    public List<CourseDtoRes> findCoursesByPublicTrue() {
        return courseMapper.toDto(courseRepository.findAllByIsPublicTrue());
    }
}
