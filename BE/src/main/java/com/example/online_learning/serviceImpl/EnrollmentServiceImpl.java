package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.EnrollmentStatus;
import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.EnrollStudentDtoReq;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


        if (!courseRepository.existsById(request.getCourseId())) {
            throw new NotFoundException("Course not found with id: " + request.getCourseId());
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
                            .course(courseRepository.getReferenceById(request.getCourseId()))
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
}
