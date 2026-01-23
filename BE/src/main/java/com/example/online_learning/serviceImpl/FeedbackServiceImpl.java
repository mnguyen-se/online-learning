package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Feedback;
import com.example.online_learning.entity.User;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.FeedbackRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepo;
    private final AssignmentSubmissionRepository submissionRepo;
    private final UserRepository userRepo;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepo, AssignmentSubmissionRepository submissionRepo, UserRepository userRepo) {
        this.feedbackRepo = feedbackRepo;
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void gradeSubmission(
            Long submissionId,
            CustomUserDetail userDetail,
            Integer score,
            String comment
    ) {
        AssignmentSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        User teacher = userRepo.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        submission.setScore(score);
        submission.setStatus(SubmissionStatus.GRADED);

        Feedback feedback = new Feedback();
        feedback.setSubmission(submission);
        feedback.setTeacher(teacher);
        feedback.setScoreGiven(score);
        feedback.setComment(comment);
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepo.save(feedback);
    }

    @Override
    public Feedback gradeSubmissionWithAI(Long submissionId, CustomUserDetail userDetail, Integer score, String comment) {
        return null;
    }
}
