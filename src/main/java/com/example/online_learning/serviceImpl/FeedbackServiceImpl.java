package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.EnrollmentStatus;
import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.CreateCourseFeedbackDtoReq;
import com.example.online_learning.dto.response.FeedbackDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.entity.Feedback;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.FeedbackMapper;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.repository.FeedbackRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepo;
    private final AssignmentSubmissionRepository submissionRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FeedbackMapper feedbackMapper;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepo,
            AssignmentSubmissionRepository submissionRepo,
            UserRepository userRepo,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            FeedbackMapper feedbackMapper
    ) {
        this.feedbackRepo = feedbackRepo;
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.feedbackMapper = feedbackMapper;
    }


    @Override
    public Feedback gradeSubmissionWithAI(Long submissionId, CustomUserDetail userDetail, Integer score, String comment) {
        return null;
    }

    @Override
    @Transactional
    public FeedbackDtoRes createCourseFeedback(CreateCourseFeedbackDtoReq request, CustomUserDetail teacherDetail) {
        User teacher = teacherDetail.getUser();
        
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new IllegalArgumentException("Only teachers can create feedback");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (course.getTeacher() == null || !course.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new IllegalArgumentException("You are not the teacher of this course");
        }

        User student = userRepo.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("User is not a student");
        }

        Enrollment enrollment = enrollmentRepository
                .findByUser_UserIdAndCourse_CourseIdAndDeletedFalse(request.getStudentId(), request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Student is not enrolled in this course"));

        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Student has not completed this course yet");
        }

        Feedback feedback = Feedback.builder()
                .teacher(teacher)
                .student(student)
                .course(course)
                .enrollment(enrollment)
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Feedback savedFeedback = feedbackRepo.save(feedback);
        return feedbackMapper.toDto(savedFeedback);
    }

    @Override
    public List<FeedbackDtoRes> getFeedbacksByStudent(Long studentId) {
        List<Feedback> feedbacks = feedbackRepo.findByStudent_UserId(studentId);
        return feedbackMapper.toDto(feedbacks);
    }

    @Override
    public List<FeedbackDtoRes> getFeedbacksByCourse(Long courseId) {
        List<Feedback> feedbacks = feedbackRepo.findByCourse_CourseId(courseId);
        return feedbackMapper.toDto(feedbacks);
    }

    @Override
    public List<FeedbackDtoRes> getFeedbacksByTeacher(CustomUserDetail teacherDetail) {
        Long teacherId = teacherDetail.getUser().getUserId();
        List<Feedback> feedbacks = feedbackRepo.findByTeacher_UserId(teacherId);
        return feedbackMapper.toDto(feedbacks);
    }
}
