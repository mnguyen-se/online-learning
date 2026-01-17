package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.Course;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.service.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Assignment createAssignment(AssignmentDtoReq req) {

        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() ->
                        new NotFoundException("Course not found with id " + req.getCourseId())
                );

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(req.getTitle())
                .description(req.getDescription())
                .maxScore(req.getMaxScore())
                .dueDate(req.getDueDate())
                .orderIndex(req.getOrderIndex())
                .build();

        return assignmentRepository.save(assignment);
    }

    @Override
    public Assignment viewAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId).orElseThrow(()
                -> new NotFoundException("Assignment not found with id " + assignmentId));
    }

    @Override
    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findByCourse_CourseId(courseId);
    }
}
