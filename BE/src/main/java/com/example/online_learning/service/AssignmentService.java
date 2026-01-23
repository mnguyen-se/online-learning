package com.example.online_learning.service;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.response.AssignmentDtoRes;
import com.example.online_learning.entity.Assignment;

import java.util.List;

public interface AssignmentService {
    AssignmentDtoRes createAssignment(AssignmentDtoReq request);
    AssignmentDtoRes viewAssignment(Long assignmentId);
    List<Assignment> findByCourseId(Long courseId);
}

