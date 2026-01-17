package com.example.online_learning.service;

import com.example.online_learning.entity.AssignmentSubmission;

public interface AssignmentSubmissionService {
    public AssignmentSubmission submit(Long assignmentId, Long userId, String content);
}
