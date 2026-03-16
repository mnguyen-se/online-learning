package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.dto.response.CourseStatisticsDtoRes;
import com.example.online_learning.dto.response.MyCoursesDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.entity.LearningProgress;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.CourseMapper;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProcessRepository learningProcessRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper,
                             UserRepository userRepository, EnrollmentRepository enrollmentRepository,
                             LearningProcessRepository learningProcessRepository,
                             AssignmentRepository assignmentRepository,
                             AssignmentSubmissionRepository assignmentSubmissionRepository) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.learningProcessRepository = learningProcessRepository;
        this.assignmentRepository = assignmentRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
    }
    @Override
    @Caching(evict = {
            @CacheEvict(value = "course:list:all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "course:list:teacher", allEntries = true)
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
            @CacheEvict(value = "course:list:all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "course:list:teacher", allEntries = true)
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
        if (dto.getImageUrl() != null) {
            course.setImageUrl(dto.getImageUrl());
        }
        if (dto.getIsPublic() != null) {
            course.setIsPublic(dto.getIsPublic());
        }

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
            value = "course:list:teacher",
            key = "#userDetail.user.userId"
    )
    public MyCoursesDtoRes getMyCourses(CustomUserDetail userDetail) {
        User teacher = userDetail.getUser();
        List<Course> courses = courseRepository.findByTeacher(teacher);
        List<CourseDtoRes> courseDtos = courseMapper.toDto(courses);

        return MyCoursesDtoRes.builder()
                .totalCourses((long) courseDtos.size())
                .courses(courseDtos)
                .build();
    }

    @Override
    public CourseDtoRes getByIdAndIsPublicTrue(Long courseId) {
        Course course = courseRepository.findByCourseIdAndIsPublicTrue(courseId);
        if(course == null) {
            throw new NotFoundException("Course not found");
        }
        return courseMapper.toDto(course);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "course:list:all", allEntries = true),
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

    @Override
    @Transactional(readOnly = true)
    public CourseStatisticsDtoRes getCourseStatistics(Long courseId, CustomUserDetail userDetail) {
        Course course = courseRepository.findByCourseId(courseId);
        if (course == null) {
            throw new NotFoundException("Course not found with id: " + courseId);
        }

        User currentUser = userDetail.getUser();
        if (course.getTeacher() == null || !course.getTeacher().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không phải là giáo viên của khóa học này");
        }

        Long activeStudents = (long) enrollmentRepository
                .findByCourse_CourseIdAndDeletedFalse(courseId)
                .size();

        List<Assignment> assignments = assignmentRepository.findByCourse_CourseId(courseId);
        List<Long> assignmentIds = assignments.stream()
                .map(Assignment::getAssignmentId)
                .collect(Collectors.toList());

        Long totalSubmissions = 0L;
        Long ungradedSubmissions = 0L;
        List<AssignmentSubmission> allSubmissions = new ArrayList<>();

        if (!assignmentIds.isEmpty()) {
            allSubmissions = assignmentSubmissionRepository.findByAssignment_AssignmentIdIn(assignmentIds);
            totalSubmissions = (long) allSubmissions.size();
            ungradedSubmissions = allSubmissions.stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED)
                    .count();
        }

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(23, 59, 59);

        List<AssignmentSubmission> weeklySubmissions = allSubmissions.stream()
                .filter(s -> {
                    LocalDateTime submittedAt = s.getSubmittedAt();
                    return submittedAt != null
                            && !submittedAt.isBefore(startDateTime)
                            && !submittedAt.isAfter(endDateTime);
                })
                .collect(Collectors.toList());

        Map<DayOfWeek, Long> submissionsByDay = weeklySubmissions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSubmittedAt().getDayOfWeek(),
                        Collectors.counting()
                ));

        String[] dayNames = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        List<CourseStatisticsDtoRes.WeeklySubmissionDto> weeklyStats = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            DayOfWeek dayOfWeek = DayOfWeek.of(i == 6 ? 7 : i + 1);
            Long count = submissionsByDay.getOrDefault(dayOfWeek, 0L);

            weeklyStats.add(CourseStatisticsDtoRes.WeeklySubmissionDto.builder()
                    .dayOfWeek(dayNames[i])
                    .count(count.intValue())
                    .build());
        }

        return CourseStatisticsDtoRes.builder()
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .activeStudents(activeStudents)
                .totalSubmissions(totalSubmissions)
                .ungradedSubmissions(ungradedSubmissions)
                .weeklySubmissions(weeklyStats)
                .build();
    }

}
