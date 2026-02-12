package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.dto.response.AnswerDetailDtoRes;
import com.example.online_learning.dto.response.FeedbackDtoRes;
import com.example.online_learning.dto.response.QuizResultDtoRes;
import com.example.online_learning.dto.response.SubmissionDetailDtoRes;
import com.example.online_learning.dto.response.SubmissionListItemDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Feedback;
import com.example.online_learning.entity.Question;
import com.example.online_learning.entity.StudentAnswer;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.FeedbackRepository;
import com.example.online_learning.repository.QuestionRepository;
import com.example.online_learning.repository.StudentAnswerRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.LearningProcessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {
    private final AssignmentSubmissionRepository submissionRepo;
    private final AssignmentRepository assignmentRepo;
    private final UserRepository userRepo;
    private final QuestionRepository questionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final LearningProcessService learningProcessService;
    private final FeedbackRepository feedbackRepository;
    private static final double COMPLETION_THRESHOLD = 70.0;

    public AssignmentSubmissionServiceImpl(
            AssignmentSubmissionRepository submissionRepo,
            AssignmentRepository assignmentRepo,
            UserRepository userRepo,
            QuestionRepository questionRepository,
            StudentAnswerRepository studentAnswerRepository,
            LearningProcessService learningProcessService,
            FeedbackRepository feedbackRepository) {
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.userRepo = userRepo;
        this.questionRepository = questionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.learningProcessService = learningProcessService;
        this.feedbackRepository = feedbackRepository;
    }

    public void submit(Long assignmentId, CustomUserDetail userDetail, String content) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);

        submissionRepo.save(submission);
    }

    @Override
    @Transactional
    public QuizResultDtoRes submitQuizAnswers(Long assignmentId, CustomUserDetail userDetail, SubmitAnswersDtoReq request) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for this assignment");
        }

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQuestionId, q -> q));

        AssignmentSubmission submission = submissionRepo
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, student.getUserId())
                .orElse(null);

        if (submission != null && submission.getStatus() != null && submission.getStatus() != SubmissionStatus.NEEDS_REVISION) {
            throw new RuntimeException("Bạn đã nộp bài rồi");
        }

        if (submission != null && submission.getStatus() == SubmissionStatus.NEEDS_REVISION) {
            studentAnswerRepository.deleteBySubmission_SubmissionId(submission.getSubmissionId());
        }

        if (submission == null) {
            submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setSubmittedAt(LocalDateTime.now());
        }

        List<StudentAnswer> studentAnswers = new ArrayList<>();

        for (SubmitAnswersDtoReq.AnswerDto answerDto : request.getAnswers()) {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                continue;
            }

            String studentAnswerStr = answerDto.getAnswer().toUpperCase().trim();

            StudentAnswer studentAnswer = StudentAnswer.builder()
                    .submission(submission)
                    .question(question)
                    .studentAnswer(studentAnswerStr)
                    .isCorrect(null)
                    .pointsEarned(null)
                    .build();

            studentAnswers.add(studentAnswer);
        }

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setScore(null);
        submission = submissionRepo.save(submission);

        studentAnswerRepository.saveAll(studentAnswers);

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                .sum();

        return QuizResultDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignmentId)
                .score(null)
                .maxScore(maxScore)
                .percentage(null)
                .details(null)
                .feedbacks(null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultDtoRes getQuizResult(Long assignmentId, CustomUserDetail userDetail) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        AssignmentSubmission submission = submissionRepo
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, student.getUserId())
                .orElseThrow(() -> new NotFoundException("Submission not found for this assignment"));

        if (submission.getStatus() == SubmissionStatus.SUBMITTED) {
            throw new RuntimeException("Bài làm đang chờ giáo viên chấm. Vui lòng chờ thông báo.");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        List<Feedback> feedbacks = feedbackRepository.findBySubmission_SubmissionId(submission.getSubmissionId());

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                .sum();

        double percentage = maxScore > 0 && submission.getScore() != null 
                ? (submission.getScore() * 100.0 / maxScore) 
                : 0.0;

        List<FeedbackDtoRes> feedbackDtos = feedbacks.stream()
                .map(feedback -> FeedbackDtoRes.builder()
                        .feedbackId(feedback.getFeedbackId())
                        .courseId(feedback.getCourse().getCourseId())
                        .courseTitle(feedback.getCourse().getTitle())
                        .studentId(feedback.getStudent().getUserId())
                        .studentName(feedback.getStudent().getName())
                        .teacherId(feedback.getTeacher().getUserId())
                        .teacherName(feedback.getTeacher().getName())
                        .comment(feedback.getComment())
                        .createdAt(feedback.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        if (submission.getStatus() == SubmissionStatus.NEEDS_REVISION) {
            return QuizResultDtoRes.builder()
                    .submissionId(submission.getSubmissionId())
                    .assignmentId(assignmentId)
                    .score(submission.getScore())
                    .maxScore(maxScore)
                    .percentage(Math.round(percentage * 100.0) / 100.0)
                    .details(null)
                    .feedbacks(feedbackDtos)
                    .build();
        }

        if (submission.getStatus() != SubmissionStatus.GRADED && submission.getStatus() != SubmissionStatus.COMPLETED) {
            throw new RuntimeException("Kết quả chưa sẵn sàng.");
        }

        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        List<AnswerDetailDtoRes> details = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                continue;
            }

            details.add(AnswerDetailDtoRes.builder()
                    .questionId(question.getQuestionId())
                    .questionText(question.getQuestionText())
                    .optionA(question.getOptionA())
                    .optionB(question.getOptionB())
                    .optionC(question.getOptionC())
                    .optionD(question.getOptionD())
                    .studentAnswer(studentAnswer.getStudentAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(studentAnswer.getIsCorrect())
                    .points(question.getPoints() != null ? question.getPoints() : 1)
                    .pointsEarned(studentAnswer.getPointsEarned())
                    .build());
        }

        return QuizResultDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignmentId)
                .score(submission.getScore())
                .maxScore(maxScore)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .details(details)
                .feedbacks(feedbackDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionListItemDtoRes> getSubmissionsByAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        List<AssignmentSubmission> submissions = submissionRepo.findByAssignment_AssignmentId(assignmentId);

        int maxScore = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId).stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                .sum();

        return submissions.stream()
                .map(submission -> SubmissionListItemDtoRes.builder()
                        .submissionId(submission.getSubmissionId())
                        .assignmentId(assignmentId)
                        .assignmentTitle(assignment.getTitle())
                        .studentId(submission.getStudent().getUserId())
                        .studentName(submission.getStudent().getName())
                        .studentEmail(submission.getStudent().getEmail())
                        .score(submission.getScore())
                        .maxScore(maxScore)
                        .status(submission.getStatus())
                        .submittedAt(submission.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionDetailDtoRes getSubmissionDetail(Long submissionId) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found with id: " + submissionId));

        Assignment assignment = submission.getAssignment();
        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignment.getAssignmentId());
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submissionId);
        List<Feedback> feedbacks = feedbackRepository.findBySubmission_SubmissionId(submissionId);

        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        List<AnswerDetailDtoRes> answerDetails = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                continue;
            }

            answerDetails.add(AnswerDetailDtoRes.builder()
                    .questionId(question.getQuestionId())
                    .questionText(question.getQuestionText())
                    .optionA(question.getOptionA())
                    .optionB(question.getOptionB())
                    .optionC(question.getOptionC())
                    .optionD(question.getOptionD())
                    .studentAnswer(studentAnswer.getStudentAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(studentAnswer.getIsCorrect())
                    .points(question.getPoints() != null ? question.getPoints() : 1)
                    .pointsEarned(studentAnswer.getPointsEarned())
                    .build());
        }

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                .sum();

        List<FeedbackDtoRes> feedbackDtos = feedbacks.stream()
                .map(feedback -> FeedbackDtoRes.builder()
                        .feedbackId(feedback.getFeedbackId())
                        .courseId(feedback.getCourse().getCourseId())
                        .courseTitle(feedback.getCourse().getTitle())
                        .studentId(feedback.getStudent().getUserId())
                        .studentName(feedback.getStudent().getName())
                        .teacherId(feedback.getTeacher().getUserId())
                        .teacherName(feedback.getTeacher().getName())
                        .comment(feedback.getComment())
                        .createdAt(feedback.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return SubmissionDetailDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getTitle())
                .studentId(submission.getStudent().getUserId())
                .studentName(submission.getStudent().getName())
                .studentEmail(submission.getStudent().getEmail())
                .studentAnswers(answerDetails)
                .score(submission.getScore())
                .maxScore(maxScore)
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .feedbacks(feedbackDtos)
                .build();
    }
}
