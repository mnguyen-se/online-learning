package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.mapper.CourseMapper;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import org.springframework.stereotype.Service;

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
    public void createCourse(CourseDtoReq dto, CustomUserDetail userDetail) {
        Course course = courseMapper.toEntity(dto);
        course.setCreatedBy(userDetail.getUser());
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findByCourseId(courseId);
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public void updateCourse(Long courseId, CourseDtoReq dto) {
        Course course = courseRepository.findByCourseId(courseId);
        courseRepository.save(courseMapper.toEntity(dto));
    }

    @Override
    public List<CourseDtoRes> getAllCourses() {
        return courseMapper.toDto(courseRepository.findAll());
    }

    @Override
    public List<CourseDtoRes> findCoursesByDeletedFalse() {
        return courseMapper.toDto(courseRepository.findByDeletedFalse());
    }
}
