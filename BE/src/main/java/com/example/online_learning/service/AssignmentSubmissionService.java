package com.example.online_learning.service;

import com.example.online_learning.security.CustomUserDetail;

public interface AssignmentSubmissionService {
    public void submit(Long assignmentId, CustomUserDetail userDetail, String content);
}
