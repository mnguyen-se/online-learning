package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.response.AssignmentDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.Course;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.AssignmentMapper;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.service.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentMapper assignmentMapper;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, CourseRepository courseRepository, AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    public AssignmentDtoRes createAssignment(Long courseId, AssignmentDtoReq req) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new NotFoundException("Course not found with id " + courseId)
                );

        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .maxScore(req.getMaxScore())
                .dueDate(req.getDueDate())
                .orderIndex(req.getOrderIndex())
                .build();
        assignmentRepository.save(assignment);
        return assignmentMapper.toDtoRes(assignment);
    }

    @Override
    public AssignmentDtoRes viewAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(()
                -> new NotFoundException("Assignment not found with id " + assignmentId));
        return assignmentMapper.toDtoRes(assignment);
    }

    @Override
    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findByCourse_CourseId(courseId);
    }
}
