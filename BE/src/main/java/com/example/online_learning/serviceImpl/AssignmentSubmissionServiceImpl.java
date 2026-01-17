package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.SubmissionStatus;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.User;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.AssignmentSubmissionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {
    private final AssignmentSubmissionRepository submissionRepo;
    private final AssignmentRepository assignmentRepo;
    private final UserRepository userRepo;

    public AssignmentSubmissionServiceImpl(AssignmentSubmissionRepository submissionRepo, AssignmentRepository assignmentRepo, UserRepository userRepo) {
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.userRepo = userRepo;
    }

    public AssignmentSubmission submit(Long assignmentId, Long userId, String content) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User student = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);

        return submissionRepo.save(submission);
    }
}
