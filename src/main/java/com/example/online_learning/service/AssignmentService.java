package com.example.online_learning.service;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.response.AssignmentDtoRes;

import java.util.List;

public interface AssignmentService {
    AssignmentDtoRes createAssignment(AssignmentDtoReq request);
    AssignmentDtoRes viewAssignment(Long assignmentId);
    List<AssignmentDtoRes> findByCourseId(Long courseId);
    List<AssignmentDtoRes> findMyAssignments(Long userId);
}

