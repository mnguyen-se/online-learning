package com.example.online_learning.mapper;

import com.example.online_learning.dto.response.AssignmentDtoRes;
import com.example.online_learning.entity.Assignment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AssignmentMapper {
    public AssignmentDtoRes toDtoRes(Assignment assignment){
        AssignmentDtoRes dto = new AssignmentDtoRes();
        dto.setTitle(assignment.getTitle());
        dto.setDueDate(assignment.getDueDate());
        dto.setDescription(assignment.getDescription());
        dto.setCourseId(assignment.getCourse().getCourseId());
        dto.setMaxScore(assignment.getMaxScore());
        dto.setOrderIndex(assignment.getOrderIndex());
        return dto;
    }

    public List<AssignmentDtoRes> toDtoRes(List<Assignment> assignments){
        List<AssignmentDtoRes> dtos = new ArrayList<>();
        for(Assignment assignment : assignments) {
            AssignmentDtoRes dto = toDtoRes(assignment);
            dtos.add(dto);
        }
        return dtos;
    }
}
