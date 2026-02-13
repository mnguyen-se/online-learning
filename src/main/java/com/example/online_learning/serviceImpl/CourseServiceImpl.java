package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.entity.LearningProgress;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.CourseMapper;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.repository.LearningProcessRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.time.LocalDateTime;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProcessRepository learningProcessRepository;
    
    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper, 
                            UserRepository userRepository, EnrollmentRepository enrollmentRepository,
                            LearningProcessRepository learningProcessRepository) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.learningProcessRepository = learningProcessRepository;
    }
    @Override
    @Caching(evict = {
            @CacheEvict(value = "courses_all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "courses_teacher", key = "#userDetail.user.userId")
    })
    public CourseDtoRes createCourse(CourseDtoReq dto, CustomUserDetail userDetail) {
        Course course = courseMapper.toEntity(dto);
        course.setCreatedBy(userDetail.getUser());
        course.setIsPublic(false);
        
        // Xử lý teacherId nếu được cung cấp
        if (dto.getTeacherId() != null) {
            User teacher = userRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + dto.getTeacherId()));
            
            // Kiểm tra user có phải là TEACHER không
            if (teacher.getRole() != UserRole.TEACHER) {
                throw new IllegalArgumentException("User with id " + dto.getTeacherId() + " is not a teacher");
            }
            
            course.setTeacher(teacher);
        }
        
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "courses_all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "courses_teacher", key = "#userDetail.user.userId")
    })
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

        // Xử lý teacherId nếu được cung cấp
        if (dto.getTeacherId() != null) {
            User teacher = userRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + dto.getTeacherId()));
            
            // Kiểm tra user có phải là TEACHER không
            if (teacher.getRole() != UserRole.TEACHER) {
                throw new IllegalArgumentException("User with id " + dto.getTeacherId() + " is not a teacher");
            }
            
            course.setTeacher(teacher);
        }

        // audit
        course.setCreatedBy(userDetail.getUser());
        course.setCreatedAt(LocalDateTime.now());

        courseRepository.save(course);
        return courseMapper.toDto(course);
    }


    @Override
    @Cacheable(value = "course:list:all")
    public List<CourseDtoRes> getAllCourses() {
        return courseMapper.toDto(courseRepository.findAll());
    }



    @Override
    @Cacheable(value = "course:list:public")
    public List<CourseDtoRes> findCoursesByPublicTrue() {
        return courseMapper.toDto(courseRepository.findAllByIsPublicTrue());
    }


    @Override
    @Cacheable(
            value = "course:list:teacher:{teacherId}",
            key = "#userDetail.user.userId"
    )
    public List<CourseDtoRes> getMyCourses(CustomUserDetail userDetail) {

        User teacher = userDetail.getUser();
        List<Course> courses = courseRepository.findByTeacher(teacher);

        return courseMapper.toDto(courses);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "courses_all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "course:list:teacher", allEntries = true)
    })
    @Transactional
    public void deleteInactiveCoursePermanently(Long courseId) {
        Course course = courseRepository.findByCourseId(courseId);
        if (course == null) {
            throw new NotFoundException("Course not found with id: " + courseId);
        }

        if (course.getIsPublic()) {
            throw new IllegalArgumentException("Cannot delete active course (isPublic = true). Only inactive courses can be deleted permanently.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseIdAndDeletedFalse(courseId);
        enrollmentRepository.deleteAll(enrollments);

        List<LearningProgress> learningProgresses = learningProcessRepository.findByCourse_CourseId(courseId);
        learningProcessRepository.deleteAll(learningProgresses);

        courseRepository.delete(course);
    }

}
