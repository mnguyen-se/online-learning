package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.AssignmentType;
import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.dto.request.GradeQuizSubmissionDtoReq;
import com.example.online_learning.dto.request.GradeWritingSubmissionDtoReq;
import com.example.online_learning.dto.request.SubmitAnswersDtoReq;
import com.example.online_learning.dto.request.SubmitWritingAnswersDtoReq;
import com.example.online_learning.dto.response.AnswerDetailDtoRes;
import com.example.online_learning.dto.response.QuizResultDtoRes;
import com.example.online_learning.dto.response.QuizSubmissionDtoRes;
import com.example.online_learning.dto.response.WritingAnswerDetailDtoRes;
import com.example.online_learning.dto.response.WritingSubmissionDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Question;
import com.example.online_learning.entity.StudentAnswer;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.repository.QuestionRepository;
import com.example.online_learning.repository.StudentAnswerRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AssignmentSubmissionService;
import com.example.online_learning.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;

    public AssignmentSubmissionServiceImpl(
            AssignmentSubmissionRepository submissionRepo,
            AssignmentRepository assignmentRepo,
            UserRepository userRepo,
            QuestionRepository questionRepository,
            StudentAnswerRepository studentAnswerRepository,
            EnrollmentRepository enrollmentRepository,
            EmailService emailService) {
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.userRepo = userRepo;
        this.questionRepository = questionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public QuizResultDtoRes submitQuizAnswers(Long assignmentId, CustomUserDetail userDetail, SubmitAnswersDtoReq request) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));
        if (assignment.getAssignmentType() != AssignmentType.QUIZ) {
            throw new IllegalArgumentException("This assignment is not a QUIZ type assignment");
        }

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        validateStudentEnrollment(student.getUserId(), assignment.getCourse().getCourseId());

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for this assignment");
        }

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Danh sách đáp án không được để trống");
        }

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQuestionId, q -> q));

        AssignmentSubmission existingSubmission = submissionRepo
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, student.getUserId())
                .orElse(null);

        AssignmentSubmission submission;
        if (existingSubmission != null) {
            submission = existingSubmission;
            List<StudentAnswer> oldAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());
            studentAnswerRepository.deleteAll(oldAnswers);
        } else {
            submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
        }
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setScore(null);
        submission = submissionRepo.save(submission);

        List<StudentAnswer> studentAnswers = new ArrayList<>();
        List<Long> notFoundQuestionIds = new ArrayList<>();

        for (SubmitAnswersDtoReq.AnswerDto answerDto : request.getAnswers()) {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                notFoundQuestionIds.add(answerDto.getQuestionId());
                continue;
            }

            String studentAnswerStr = answerDto.getAnswer() != null ? answerDto.getAnswer().toUpperCase().trim() : null;
            if (studentAnswerStr == null || studentAnswerStr.isEmpty()) {
                throw new RuntimeException("Đáp án không được để trống cho questionId: " + answerDto.getQuestionId());
            }

            StudentAnswer studentAnswer = StudentAnswer.builder()
                    .submission(submission)
                    .question(question)
                    .studentAnswer(studentAnswerStr)
                    .isCorrect(null)
                    .pointsEarned(null)
                    .build();

            studentAnswers.add(studentAnswer);
        }

        if (!notFoundQuestionIds.isEmpty()) {
            throw new NotFoundException("Không tìm thấy câu hỏi với các ID: " + notFoundQuestionIds);
        }

        if (studentAnswers.isEmpty()) {
            throw new RuntimeException("Không có đáp án hợp lệ nào được tạo. Vui lòng kiểm tra lại questionId trong request.");
        }

        studentAnswerRepository.saveAll(studentAnswers);

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 5)
                .sum();

        return QuizResultDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignmentId)
                .score(null)
                .maxScore(maxScore)
                .percentage(null)
                .details(new ArrayList<>())
                .feedbacks(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultDtoRes getQuizResult(Long assignmentId, CustomUserDetail userDetail) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));
        if (assignment.getAssignmentType() != AssignmentType.QUIZ) {
            throw new IllegalArgumentException("This assignment is not a QUIZ type assignment");
        }

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        validateStudentEnrollment(student.getUserId(), assignment.getCourse().getCourseId());

        AssignmentSubmission submission = submissionRepo
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, student.getUserId())
                .orElseThrow(() -> new NotFoundException("Bạn chưa nộp bài cho assignment này"));
        if (submission.getStatus() != SubmissionStatus.GRADED) {
            throw new RuntimeException("Bài làm của bạn chưa được giáo viên chấm điểm. Vui lòng chờ giáo viên chấm.");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 5)
                .sum();

        double percentage = maxScore > 0 && submission.getScore() != null 
                ? (submission.getScore() * 100.0 / maxScore) 
                : 0.0;

        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        List<AnswerDetailDtoRes> details = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                continue;
            }

            details.add(AnswerDetailDtoRes.builder()
                    .answerId(studentAnswer.getAnswerId())
                    .questionId(question.getQuestionId())
                    .questionText(question.getQuestionText())
                    .optionA(question.getOptionA())
                    .optionB(question.getOptionB())
                    .optionC(question.getOptionC())
                    .optionD(question.getOptionD())
                    .studentAnswer(studentAnswer.getStudentAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(studentAnswer.getIsCorrect())
                    .points(question.getPoints() != null ? question.getPoints() : 5)
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
                .feedbacks(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSubmissionDtoRes> getQuizSubmissions(Long assignmentId, CustomUserDetail userDetail) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));
        if (assignment.getAssignmentType() != AssignmentType.QUIZ) {
            throw new IllegalArgumentException("This assignment is not a QUIZ type assignment");
        }

        List<AssignmentSubmission> submissions = submissionRepo.findByAssignment_AssignmentId(assignmentId);
        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);

        return submissions.stream()
                .map(submission -> buildQuizSubmissionDtoRes(submission, questions))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizSubmissionDtoRes getQuizSubmission(Long submissionId, CustomUserDetail userDetail) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found with id: " + submissionId));
        Assignment assignment = submission.getAssignment();
        if (assignment.getAssignmentType() != AssignmentType.QUIZ) {
            throw new IllegalArgumentException("This submission is not for a QUIZ type assignment");
        }

        User currentUser = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = assignment.getCourse();
        if (course.getTeacher() == null || !course.getTeacher().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không phải là giáo viên của khóa học này");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignment.getAssignmentId());
        return buildQuizSubmissionDtoRes(submission, questions);
    }

    @Override
    @Transactional
    public QuizResultDtoRes gradeQuizSubmission(Long submissionId, CustomUserDetail userDetail, GradeQuizSubmissionDtoReq request) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found with id: " + submissionId));
        Assignment assignment = submission.getAssignment();
        if (assignment.getAssignmentType() != AssignmentType.QUIZ) {
            throw new IllegalArgumentException("This submission is not for a QUIZ type assignment");
        }

        User currentUser = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = assignment.getCourse();
        if (course.getTeacher() == null || !course.getTeacher().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không phải là giáo viên của khóa học này");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignment.getAssignmentId());

        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submissionId);
        
        Map<Long, StudentAnswer> answerByQuestionMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        Map<Long, GradeQuizSubmissionDtoReq.AnswerGradeDto> answerGradeMap = new HashMap<>();
        if (request.getAnswerGrades() != null) {
            for (GradeQuizSubmissionDtoReq.AnswerGradeDto answerGrade : request.getAnswerGrades()) {
                answerGradeMap.put(answerGrade.getAnswerId(), answerGrade);
            }
        }

        int totalScore = 0;
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerByQuestionMap.get(question.getQuestionId());
            boolean isNewAnswer = false;
            
            if (studentAnswer == null) {
                studentAnswer = StudentAnswer.builder()
                        .submission(submission)
                        .question(question)
                        .studentAnswer("")
                        .isCorrect(false)
                        .pointsEarned(0)
                        .build();
                isNewAnswer = true;
            } else {
                String studentAnswerStr = studentAnswer.getStudentAnswer() != null 
                        ? studentAnswer.getStudentAnswer().trim().toUpperCase() 
                        : "";
                String correctAnswerStr = question.getCorrectAnswer() != null 
                        ? question.getCorrectAnswer().trim().toUpperCase() 
                        : "";
                
                boolean isCorrect = !studentAnswerStr.isEmpty() && studentAnswerStr.equals(correctAnswerStr);
                studentAnswer.setIsCorrect(isCorrect);
                
                int questionPoints = question.getPoints() != null ? question.getPoints() : 5;
                int pointsEarned = isCorrect ? questionPoints : 0;
                
                GradeQuizSubmissionDtoReq.AnswerGradeDto answerGrade = answerGradeMap.get(studentAnswer.getAnswerId());
                if (answerGrade != null && answerGrade.getPointsEarned() != null) {
                    pointsEarned = answerGrade.getPointsEarned();
                }
                
                studentAnswer.setPointsEarned(pointsEarned);
            }
            
            totalScore += studentAnswer.getPointsEarned();
            
            if (isNewAnswer) {
                studentAnswer = studentAnswerRepository.save(studentAnswer);
            } else {
                studentAnswerRepository.save(studentAnswer);
            }
        }

        Integer finalScore = request.getScore() != null ? request.getScore() : totalScore;
        
        submission.setScore(finalScore);
        submission.setStatus(SubmissionStatus.GRADED);
        if (request.getFeedback() != null && !request.getFeedback().trim().isEmpty()) {
            submission.setContent(request.getFeedback());
        }
        submission = submissionRepo.save(submission);

        List<StudentAnswer> updatedAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submissionId);
        Map<Long, StudentAnswer> updatedAnswerMap = updatedAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        int maxScore = questions.stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 5)
                .sum();
        double percentage = maxScore > 0 ? (finalScore * 100.0 / maxScore) : 0.0;

        List<AnswerDetailDtoRes> details = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = updatedAnswerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                details.add(AnswerDetailDtoRes.builder()
                        .answerId(null)
                        .questionId(question.getQuestionId())
                        .questionText(question.getQuestionText())
                        .optionA(question.getOptionA())
                        .optionB(question.getOptionB())
                        .optionC(question.getOptionC())
                        .optionD(question.getOptionD())
                        .studentAnswer(null)
                        .correctAnswer(question.getCorrectAnswer())
                        .isCorrect(false)
                        .points(question.getPoints() != null ? question.getPoints() : 5)
                        .pointsEarned(0)
                        .build());
            } else {
                details.add(AnswerDetailDtoRes.builder()
                        .answerId(studentAnswer.getAnswerId())
                        .questionId(question.getQuestionId())
                        .questionText(question.getQuestionText())
                        .optionA(question.getOptionA())
                        .optionB(question.getOptionB())
                        .optionC(question.getOptionC())
                        .optionD(question.getOptionD())
                        .studentAnswer(studentAnswer.getStudentAnswer())
                        .correctAnswer(question.getCorrectAnswer())
                        .isCorrect(studentAnswer.getIsCorrect())
                        .points(question.getPoints() != null ? question.getPoints() : 5)
                        .pointsEarned(studentAnswer.getPointsEarned())
                        .build());
            }
        }

        try {
            emailService.sendQuizResult(
                    submission.getStudent().getEmail(),
                    submission.getStudent().getName(),
                    assignment.getTitle(),
                    submission.getScore(),
                    maxScore,
                    submission.getContent(),
                    details
            );
        } catch (Exception e) {
            System.err.println("Failed to send quiz email: " + e.getMessage());
        }

        return QuizResultDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(assignment.getAssignmentId())
                .score(finalScore)
                .maxScore(maxScore)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .details(details)
                .feedbacks(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional
    public WritingSubmissionDtoRes submitWritingAnswers(Long assignmentId, CustomUserDetail userDetail, SubmitWritingAnswersDtoReq request) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        // Kiểm tra assignment phải là WRITING type
        if (assignment.getAssignmentType() != AssignmentType.WRITING) {
            throw new IllegalArgumentException("This assignment is not a WRITING type assignment");
        }

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        validateStudentEnrollment(student.getUserId(), assignment.getCourse().getCourseId());

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for this assignment");
        }

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Danh sách đáp án không được để trống");
        }

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQuestionId, q -> q));

        AssignmentSubmission existingSubmission = submissionRepo
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, student.getUserId())
                .orElse(null);

        AssignmentSubmission submission;
        if (existingSubmission != null) {
            submission = existingSubmission;
            List<StudentAnswer> oldAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());
            studentAnswerRepository.deleteAll(oldAnswers);
        } else {
            submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
        }
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED); // Chờ giáo viên chấm
        submission.setScore(null); // Chưa có điểm
        submission = submissionRepo.save(submission);

        List<StudentAnswer> studentAnswers = new ArrayList<>();
        for (SubmitWritingAnswersDtoReq.AnswerDto answerDto : request.getAnswers()) {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                throw new NotFoundException("Question not found with id: " + answerDto.getQuestionId());
            }

            String studentAnswerText = null;
            
            if (question.getQuestionType() == null) {
                throw new RuntimeException("Question type is null for questionId: " + answerDto.getQuestionId() + ". Vui lòng kiểm tra lại câu hỏi.");
            }
            
            if (question.getQuestionType() == com.example.online_learning.constants.QuestionType.REORDER) {
                if (answerDto.getOrderedItems() == null || answerDto.getOrderedItems().isEmpty()) {
                    throw new RuntimeException("Danh sách sắp xếp không được để trống cho questionId: " + answerDto.getQuestionId());
                }
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    studentAnswerText = mapper.writeValueAsString(answerDto.getOrderedItems());
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi khi xử lý đáp án sắp xếp: " + e.getMessage());
                }
            } else if (question.getQuestionType() == com.example.online_learning.constants.QuestionType.MATCHING) {
                if (answerDto.getMatchingPairs() == null || answerDto.getMatchingPairs().isEmpty()) {
                    throw new RuntimeException("Danh sách nối cột không được để trống cho questionId: " + answerDto.getQuestionId());
                }
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    studentAnswerText = mapper.writeValueAsString(answerDto.getMatchingPairs());
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi khi xử lý đáp án nối cột: " + e.getMessage());
                }
            } else if (question.getQuestionType() == com.example.online_learning.constants.QuestionType.FILL_BLANK || 
                       question.getQuestionType() == com.example.online_learning.constants.QuestionType.ESSAY_WRITING) {
                if (answerDto.getAnswer() == null || answerDto.getAnswer().trim().isEmpty()) {
                    throw new RuntimeException("Đáp án không được để trống cho questionId: " + answerDto.getQuestionId());
                }
                studentAnswerText = answerDto.getAnswer().trim();
            } else {
                throw new RuntimeException("Loại câu hỏi không được hỗ trợ: " + question.getQuestionType() + " cho questionId: " + answerDto.getQuestionId());
            }

            StudentAnswer studentAnswer = StudentAnswer.builder()
                    .submission(submission)
                    .question(question)
                    .studentAnswer(studentAnswerText)
                    .isCorrect(null) // Chưa chấm
                    .pointsEarned(null) // Chưa chấm
                    .build();

            studentAnswers.add(studentAnswer);
        }

        studentAnswerRepository.saveAll(studentAnswers);

        return buildWritingSubmissionDtoRes(submission, questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WritingSubmissionDtoRes> getWritingSubmissions(Long assignmentId, CustomUserDetail userDetail) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

       
        if (assignment.getAssignmentType() != AssignmentType.WRITING) {
            throw new IllegalArgumentException("This assignment is not a WRITING type assignment");
        }

       
        List<AssignmentSubmission> submissions = submissionRepo.findByAssignment_AssignmentId(assignmentId);
        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);

        return submissions.stream()
                .map(submission -> buildWritingSubmissionDtoRes(submission, questions))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WritingSubmissionDtoRes getWritingSubmission(Long submissionId, CustomUserDetail userDetail) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found with id: " + submissionId));

        Assignment assignment = submission.getAssignment();
        if (assignment.getAssignmentType() != AssignmentType.WRITING) {
            throw new IllegalArgumentException("This submission is not for a WRITING type assignment");
        }

        // Chỉ TEACHER mới xem được (đã kiểm tra trong @PreAuthorize)
        User currentUser = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Kiểm tra TEACHER phải là teacher của course
        Course course = assignment.getCourse();
        if (course.getTeacher() == null || !course.getTeacher().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không phải là giáo viên của khóa học này");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignment.getAssignmentId());
        return buildWritingSubmissionDtoRes(submission, questions);
    }

    @Override
    @Transactional
    public WritingSubmissionDtoRes gradeWritingSubmission(Long submissionId, CustomUserDetail userDetail, GradeWritingSubmissionDtoReq request) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found with id: " + submissionId));

        Assignment assignment = submission.getAssignment();
        if (assignment.getAssignmentType() != AssignmentType.WRITING) {
            throw new IllegalArgumentException("This submission is not for a WRITING type assignment");
        }

        // Chỉ TEACHER mới chấm được (kiểm tra trong @PreAuthorize)
        User currentUser = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Kiểm tra TEACHER phải là teacher của course
        Course course = assignment.getCourse();
        if (course.getTeacher() == null || !course.getTeacher().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không phải là giáo viên của khóa học này");
        }

        if (submission.getStatus() == SubmissionStatus.GRADED) {
            // Nếu đã chấm rồi, cho phép chấm lại (cập nhật điểm)
        }

        // Cập nhật điểm tổng
        submission.setScore(request.getScore());
        submission.setStatus(SubmissionStatus.GRADED);
        if (request.getFeedback() != null && !request.getFeedback().trim().isEmpty()) {
            submission.setContent(request.getFeedback()); // Lưu feedback vào content field
        }
        submission = submissionRepo.save(submission);

        // Cập nhật điểm từng câu
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submissionId);
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(StudentAnswer::getAnswerId, sa -> sa));

        for (GradeWritingSubmissionDtoReq.AnswerGradeDto answerGrade : request.getAnswerGrades()) {
            StudentAnswer studentAnswer = answerMap.get(answerGrade.getAnswerId());
            if (studentAnswer == null) {
                throw new NotFoundException("Student answer not found with id: " + answerGrade.getAnswerId());
            }

            studentAnswer.setPointsEarned(answerGrade.getPointsEarned());
            studentAnswer.setIsCorrect(answerGrade.getIsCorrect());
            studentAnswerRepository.save(studentAnswer);
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignment.getAssignmentId());
        
        WritingSubmissionDtoRes submissionDto = buildWritingSubmissionDtoRes(submission, questions);
        
        System.out.println("=== ATTEMPTING TO SEND EMAIL ===");
        System.out.println("Email Service: " + (emailService != null ? "OK" : "NULL"));
        System.out.println("Student Email: " + submission.getStudent().getEmail());
        
        try {
            emailService.sendWritingAssignmentResult(
                submission.getStudent().getEmail(),
                submission.getStudent().getName(),
                assignment.getTitle(),
                submission.getScore(),
                assignment.getMaxScore(),
                submission.getContent(),
                submissionDto.getAnswers()
            );
            System.out.println("✅ Email service called successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return submissionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public WritingSubmissionDtoRes getWritingResult(Long assignmentId, CustomUserDetail userDetail) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        if (assignment.getAssignmentType() != AssignmentType.WRITING) {
            throw new IllegalArgumentException("This assignment is not a WRITING type assignment");
        }

        User student = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        validateStudentEnrollment(student.getUserId(), assignment.getCourse().getCourseId());

        AssignmentSubmission submission = submissionRepo
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, student.getUserId())
                .orElseThrow(() -> new NotFoundException("Bạn chưa nộp bài cho assignment này"));

        // Chỉ xem được khi đã được chấm (GRADED)
        if (submission.getStatus() != SubmissionStatus.GRADED) {
            throw new RuntimeException("Bài làm của bạn chưa được giáo viên chấm điểm. Vui lòng chờ giáo viên chấm.");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        return buildWritingSubmissionDtoRes(submission, questions);
    }

    private QuizSubmissionDtoRes buildQuizSubmissionDtoRes(AssignmentSubmission submission, List<Question> questions) {
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        List<AnswerDetailDtoRes> answerDetails = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                continue;
            }
            answerDetails.add(AnswerDetailDtoRes.builder()
                    .answerId(studentAnswer.getAnswerId())
                    .questionId(question.getQuestionId())
                    .questionText(question.getQuestionText())
                    .optionA(question.getOptionA())
                    .optionB(question.getOptionB())
                    .optionC(question.getOptionC())
                    .optionD(question.getOptionD())
                    .studentAnswer(studentAnswer.getStudentAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(studentAnswer.getIsCorrect())
                    .points(question.getPoints() != null ? question.getPoints() : 5)
                    .pointsEarned(studentAnswer.getPointsEarned())
                    .build());
        }

        Integer maxScore = submission.getAssignment().getMaxScore();
        if (maxScore == null) {
            maxScore = questions.stream()
                    .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 5)
                    .sum();
        }

        return QuizSubmissionDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(submission.getAssignment().getAssignmentId())
                .studentId(submission.getStudent().getUserId())
                .studentName(submission.getStudent().getName())
                .studentEmail(submission.getStudent().getEmail())
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .maxScore(maxScore)
                .status(submission.getStatus() != null ? submission.getStatus().name() : SubmissionStatus.SUBMITTED.name())
                .feedback(submission.getContent() != null ? submission.getContent() : "")
                .answers(answerDetails)
                .build();
    }

    private void validateStudentEnrollment(Long studentId, Long courseId) {
        enrollmentRepository.findByUser_UserIdAndCourse_CourseIdAndDeletedFalse(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa enroll khóa học này"));
    }

    private WritingSubmissionDtoRes buildWritingSubmissionDtoRes(AssignmentSubmission submission, List<Question> questions) {
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findBySubmission_SubmissionId(submission.getSubmissionId());
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getQuestionId(), sa -> sa));

        List<WritingAnswerDetailDtoRes> answerDetails = new ArrayList<>();
        for (Question question : questions) {
            StudentAnswer studentAnswer = answerMap.get(question.getQuestionId());
            if (studentAnswer == null) {
                continue;
            }

            answerDetails.add(WritingAnswerDetailDtoRes.builder()
                    .answerId(studentAnswer.getAnswerId())
                    .questionId(question.getQuestionId())
                    .questionText(question.getQuestionText())
                    .questionType(question.getQuestionType())
                    .studentAnswer(studentAnswer.getStudentAnswer())
                    .points(question.getPoints() != null ? question.getPoints() : 5)
                    .pointsEarned(studentAnswer.getPointsEarned())
                    .isCorrect(studentAnswer.getIsCorrect())
                    .sampleAnswer(question.getCorrectAnswer())
                    .build());
        }

        Integer maxScore = submission.getAssignment().getMaxScore();
        if (maxScore == null) {
            maxScore = questions.stream()
                    .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                    .sum();
        }

        return WritingSubmissionDtoRes.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(submission.getAssignment().getAssignmentId())
                .studentId(submission.getStudent().getUserId())
                .studentName(submission.getStudent().getName())
                .studentEmail(submission.getStudent().getEmail())
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .maxScore(maxScore)
                .status(submission.getStatus() != null ? submission.getStatus().name() : SubmissionStatus.SUBMITTED.name())
                .feedback(submission.getContent() != null ? submission.getContent() : "")
                .answers(answerDetails)
                .build();
    }

}
