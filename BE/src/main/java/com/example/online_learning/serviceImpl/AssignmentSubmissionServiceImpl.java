package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.dto.response.AnswerDetailDtoRes;
import com.example.online_learning.dto.response.QuizResultDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Question;
import com.example.online_learning.entity.StudentAnswer;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.QuestionRepository;
import com.example.online_learning.repository.StudentAnswerRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentSubmissionService;
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

    public AssignmentSubmissionServiceImpl(
            AssignmentSubmissionRepository submissionRepo,
            AssignmentRepository assignmentRepo,
            UserRepository userRepo,
            QuestionRepository questionRepository,
            StudentAnswerRepository studentAnswerRepository) {
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.userRepo = userRepo;
        this.questionRepository = questionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
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

        if (submission == null) {
            submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setSubmittedAt(LocalDateTime.now());
        } else {
            studentAnswerRepository.deleteBySubmission_SubmissionId(submission.getSubmissionId());
        }

        List<StudentAnswer> studentAnswers = new ArrayList<>();
        int totalScore = 0;
        int correctCount = 0;
        int wrongCount = 0;

        for (SubmitAnswersDtoReq.AnswerDto answerDto : request.getAnswers()) {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                continue;
            }

            String studentAnswerStr = answerDto.getAnswer().toUpperCase().trim();
            String correctAnswerStr = question.getCorrectAnswer().toUpperCase().trim();
            boolean isCorrect = studentAnswerStr.equals(correctAnswerStr);

            int pointsEarned = isCorrect ? (question.getPoints() != null ? question.getPoints() : 1) : 0;
            totalScore += pointsEarned;

            if (isCorrect) {
                correctCount++;
            } else {
                wrongCount++;
            }

            StudentAnswer studentAnswer = StudentAnswer.builder()
                    .submission(submission)
                    .question(question)
                    .studentAnswer(studentAnswerStr)
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .build();

            studentAnswers.add(studentAnswer);
        }

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                .sum();

        submission.setScore(totalScore);
        submission.setStatus(SubmissionStatus.GRADED);
        submission = submissionRepo.save(submission);

        studentAnswerRepository.saveAll(studentAnswers);

        List<AnswerDetailDtoRes> details = studentAnswers.stream()
                .map(sa -> {
                    Question q = sa.getQuestion();
                    return AnswerDetailDtoRes.builder()
                            .questionId(q.getQuestionId())
                            .questionText(q.getQuestionText())
                            .optionA(q.getOptionA())
                            .optionB(q.getOptionB())
                            .optionC(q.getOptionC())
                            .optionD(q.getOptionD())
                            .studentAnswer(sa.getStudentAnswer())
                            .correctAnswer(q.getCorrectAnswer())
                            .isCorrect(sa.getIsCorrect())
                            .points(q.getPoints() != null ? q.getPoints() : 1)
                            .pointsEarned(sa.getPointsEarned())
                            .build();
                })
                .collect(Collectors.toList());

        double percentage = maxScore > 0 ? (totalScore * 100.0 / maxScore) : 0.0;

        return QuizResultDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignmentId)
                .totalQuestions(questions.size())
                .correctAnswers(correctCount)
                .wrongAnswers(wrongCount)
                .score(totalScore)
                .maxScore(maxScore)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .details(details)
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

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());

        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        int correctCount = 0;
        int wrongCount = 0;

        List<AnswerDetailDtoRes> details = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                continue;
            }

            if (studentAnswer.getIsCorrect()) {
                correctCount++;
            } else {
                wrongCount++;
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

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                .sum();

        double percentage = maxScore > 0 ? (submission.getScore() * 100.0 / maxScore) : 0.0;

        return QuizResultDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignmentId)
                .totalQuestions(questions.size())
                .correctAnswers(correctCount)
                .wrongAnswers(wrongCount)
                .score(submission.getScore())
                .maxScore(maxScore)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .details(details)
                .build();
    }
}
