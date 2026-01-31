package com.example.online_learning.mapper;

import com.example.online_learning.dto.response.AssignmentDtoRes;
import com.example.online_learning.entity.Assignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {
    public AssignmentDtoRes toDtoRes(Assignment assignment){
        AssignmentDtoRes dto = new AssignmentDtoRes();
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setTitle(assignment.getTitle());
        dto.setDueDate(assignment.getDueDate());
        dto.setDescription(assignment.getDescription());
        dto.setCourseId(assignment.getCourse().getCourseId());
        dto.setMaxScore(assignment.getMaxScore());
        dto.setOrderIndex(assignment.getOrderIndex());
        return dto;
    }
}
