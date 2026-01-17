package com.example.online_learning.service;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.entity.Assignment;

import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(AssignmentDtoReq request);
    Assignment viewAssignment(Long assignmentId);
    List<Assignment> findByCourseId(Long courseId);
}

