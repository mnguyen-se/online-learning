package com.example.online_learning.serviceImpl;

import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentCompletion;
import com.example.online_learning.repository.AssignmentCompletionRepository;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.AssignmentCompletionService;
import com.example.online_learning.service.LearningProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentCompletionServiceImpl
        implements AssignmentCompletionService {

    private final AssignmentCompletionRepository completionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final LearningProcessService learningProcessService;

    @Override
    public void completeAssignment(Long assignmentId, Long userId) {

        if (completionRepository
                .existsByUser_UserIdAndAssignment_AssignmentId(
                        userId, assignmentId)) {
            return;
        }

        Assignment assignment =
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Assignment not found")
                        );

        AssignmentCompletion completion = AssignmentCompletion.builder()
                .user(userRepository.getReferenceById(userId))
                .assignment(assignment)
                .completedAt(LocalDateTime.now())
                .build();

        completionRepository.save(completion);

        // 🔥 CỘNG PROGRESS
        learningProcessService.increaseProgress(
                assignment.getCourse().getCourseId(),
                userId
        );
    }
}

