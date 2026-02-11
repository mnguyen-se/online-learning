package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.CourseMapper;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.CourseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    
    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.userRepository = userRepository;
    }
    @Override
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
    @Cacheable(value = "courses", key = "'all'")
    public List<CourseDtoRes> getAllCourses() {
        return courseMapper.toDto(courseRepository.findAll());
    }


    @Override
    @Cacheable(value = "courses", key = "'public'")
    public List<CourseDtoRes> findCoursesByPublicTrue() {
        return courseMapper.toDto(courseRepository.findAllByIsPublicTrue());
    }


    @Override
    @Cacheable(value = "courses", key = "'teacher_' + #userDetail.user.userId")
    public List<CourseDtoRes> getMyCourses(CustomUserDetail userDetail) {

        User teacher = userDetail.getUser();
        List<Course> courses = courseRepository.findByTeacher(teacher);

        return courseMapper.toDto(courses);
    }

}
