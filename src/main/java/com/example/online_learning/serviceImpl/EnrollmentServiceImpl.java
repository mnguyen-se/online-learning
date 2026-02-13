package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.EnrollmentStatus;
import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.EnrollStudentDtoReq;
import com.example.online_learning.dto.response.EnrolledCourseDtoRes;
import com.example.online_learning.dto.response.EnrolledStudentDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public void enrollStudent(EnrollStudentDtoReq request) {

        User student = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new NotFoundException("Student not found with username: " + request.getUsername()));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("User is not a student");
        }

        Course course = courseRepository.findByCourseId(request.getCourseId());
        if (course == null) {
            throw new NotFoundException("Course not found with id: " + request.getCourseId());
        }

        if (!course.getIsPublic()) {
            throw new IllegalArgumentException("Cannot enroll student to inactive course (isPublic = false)");
        }

        if (enrollmentRepository.findByUser_UserNameAndCourse_CourseIdAndDeletedFalse(
                request.getUsername(), request.getCourseId()
        ).isPresent()) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        enrollmentRepository.findByUser_UserIdAndCourse_CourseId(
                student.getUserId(), request.getCourseId()
        ).ifPresentOrElse(
                enrollment -> {
                    if (enrollment.getDeleted()) {
                        enrollment.setDeleted(false);
                        enrollment.setStatus(EnrollmentStatus.ACTIVE);
                        enrollmentRepository.save(enrollment);
                    }
                },
                () -> {
                    Enrollment newEnrollment = Enrollment.builder()
                            .user(student)
                            .course(course)
                            .status(EnrollmentStatus.ACTIVE)
                            .deleted(false)
                            .build();
                    enrollmentRepository.save(newEnrollment);
                }
        );
    }

    @Override
    public void unenrollStudent(Long courseId, String username) {
        // Tìm user theo username
        User student = userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("Student not found with username: " + username));

        // Kiểm tra course có tồn tại không
        if (!courseRepository.existsById(courseId)) {
            throw new NotFoundException("Course not found with id: " + courseId);
        }

        // Tìm enrollment và soft delete
        Enrollment enrollment = enrollmentRepository
                .findByUser_UserNameAndCourse_CourseIdAndDeletedFalse(username, courseId)
                .orElseThrow(() -> new NotFoundException(
                        "Student is not enrolled in this course"
                ));

        enrollment.setDeleted(true);
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolledStudentDtoRes> getEnrolledStudentsByCourseId(Long courseId) {
        // Kiểm tra course có tồn tại không
        if (!courseRepository.existsById(courseId)) {
            throw new NotFoundException("Course not found with id: " + courseId);
        }

        // Lấy danh sách enrollments theo courseId và chưa bị xóa
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseIdAndDeletedFalse(courseId);

        // Chuyển đổi sang DTO
        return enrollments.stream()
                .map(enrollment -> {
                    User student = enrollment.getUser();
                    EnrolledStudentDtoRes dto = new EnrolledStudentDtoRes();
                    dto.setEnrollmentId(enrollment.getEnrollmentId());
                    dto.setStudentId(student.getUserId());
                    dto.setUsername(student.getUserName());
                    dto.setName(student.getName());
                    dto.setEmail(student.getEmail());
                    dto.setAddress(student.getAddress());
                    dto.setStatus(enrollment.getStatus());
                    dto.setEnrolledAt(enrollment.getEnrolledAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolledCourseDtoRes> getEnrolledCoursesByUsername(String username) {
        // Kiểm tra user có tồn tại không
        User student = userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("Student not found with username: " + username));

        // Lấy danh sách enrollments theo username và chưa bị xóa
        List<Enrollment> enrollments = enrollmentRepository.findByUser_UserNameAndDeletedFalse(username);

        // Chuyển đổi sang DTO
        return enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    EnrolledCourseDtoRes dto = new EnrolledCourseDtoRes();
                    dto.setEnrollmentId(enrollment.getEnrollmentId());
                    dto.setCourseId(course.getCourseId());
                    dto.setCourseTitle(course.getTitle());
                    dto.setCourseDescription(course.getDescription());
                    dto.setIsPublic(course.getIsPublic());
                    dto.setCourseCreatedAt(course.getCreatedAt());
                    dto.setEnrollmentStatus(enrollment.getStatus());
                    dto.setEnrolledAt(enrollment.getEnrolledAt());
                    if (course.getTeacher() != null) {
                        dto.setTeacherId(course.getTeacher().getUserId());
                        dto.setTeacherName(course.getTeacher().getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolledCourseDtoRes> getMyEnrolledCourses(CustomUserDetail userDetail) {
        // Lấy username từ user đã đăng nhập
        String username = userDetail.getUsername();
        
        // Lấy danh sách enrollments theo username và chưa bị xóa
        List<Enrollment> enrollments = enrollmentRepository.findByUser_UserNameAndDeletedFalse(username);

        // Chuyển đổi sang DTO
        return enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    EnrolledCourseDtoRes dto = new EnrolledCourseDtoRes();
                    dto.setEnrollmentId(enrollment.getEnrollmentId());
                    dto.setCourseId(course.getCourseId());
                    dto.setCourseTitle(course.getTitle());
                    dto.setCourseDescription(course.getDescription());
                    dto.setIsPublic(course.getIsPublic());
                    dto.setCourseCreatedAt(course.getCreatedAt());
                    dto.setEnrollmentStatus(enrollment.getStatus());
                    dto.setEnrolledAt(enrollment.getEnrolledAt());
                    if (course.getTeacher() != null) {
                        dto.setTeacherId(course.getTeacher().getUserId());
                        dto.setTeacherName(course.getTeacher().getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
