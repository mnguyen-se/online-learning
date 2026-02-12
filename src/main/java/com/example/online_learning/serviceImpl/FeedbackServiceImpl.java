package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.EnrollmentStatus;
import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.CreateCourseFeedbackDtoReq;
import com.example.online_learning.dto.response.FeedbackDtoRes;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.entity.Feedback;
import com.example.online_learning.entity.Question;
import com.example.online_learning.entity.StudentAnswer;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.FeedbackMapper;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.repository.FeedbackRepository;
import com.example.online_learning.repository.QuestionRepository;
import com.example.online_learning.repository.StudentAnswerRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepo;
    private final AssignmentSubmissionRepository submissionRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FeedbackMapper feedbackMapper;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepo,
            AssignmentSubmissionRepository submissionRepo,
            UserRepository userRepo,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            FeedbackMapper feedbackMapper,
            StudentAnswerRepository studentAnswerRepository,
            QuestionRepository questionRepository
    ) {
        this.feedbackRepo = feedbackRepo;
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.feedbackMapper = feedbackMapper;
        this.studentAnswerRepository = studentAnswerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void gradeQuizSubmission(
            Long submissionId,
            CustomUserDetail userDetail,
            Integer score,
            String comment
    ) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));

        User teacher = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("Teacher not found"));

        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submissionId);
        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(
                submission.getAssignment().getAssignmentId());

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQuestionId, q -> q));

        int totalScore = 0;
        for (StudentAnswer studentAnswer : studentAnswers) {
            Question question = questionMap.get(studentAnswer.getQuestion().getQuestionId());
            if (question == null) {
                continue;
            }

            String studentAnswerStr = studentAnswer.getStudentAnswer().toUpperCase().trim();
            String correctAnswerStr = question.getCorrectAnswer().toUpperCase().trim();
            boolean isCorrect = studentAnswerStr.equals(correctAnswerStr);

            int pointsEarned = isCorrect ? (question.getPoints() != null ? question.getPoints() : 1) : 0;

            studentAnswer.setIsCorrect(isCorrect);
            studentAnswer.setPointsEarned(pointsEarned);
            
            totalScore += pointsEarned;
        }

        studentAnswerRepository.saveAll(studentAnswers);

        if (score != null) {
            submission.setScore(score);
        } else {
            submission.setScore(totalScore);
        }

        submission.setStatus(SubmissionStatus.GRADED);
        submissionRepo.save(submission);

        Feedback feedback = Feedback.builder()
                .submission(submission)
                .teacher(teacher)
                .student(submission.getStudent())
                .course(submission.getAssignment().getCourse())
                .comment(comment != null ? comment : "")
                .createdAt(LocalDateTime.now())
                .build();

        feedbackRepo.save(feedback);
    }

    @Override
    @Transactional
    public void requestRevision(
            Long submissionId,
            CustomUserDetail userDetail,
            String comment
    ) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));

        User teacher = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("Teacher not found"));

        submission.setStatus(SubmissionStatus.NEEDS_REVISION);
        submissionRepo.save(submission);

        Feedback feedback = Feedback.builder()
                .submission(submission)
                .teacher(teacher)
                .student(submission.getStudent())
                .course(submission.getAssignment().getCourse())
                .comment(comment != null ? comment : "")
                .createdAt(LocalDateTime.now())
                .build();

        feedbackRepo.save(feedback);
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
