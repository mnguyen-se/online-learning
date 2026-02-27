package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.AssignmentDtoReq;
import com.example.online_learning.dto.response.AssignmentDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Enrollment;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.AssignmentMapper;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.CourseRepository;
import com.example.online_learning.repository.EnrollmentRepository;
import com.example.online_learning.service.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentMapper assignmentMapper;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    public AssignmentDtoRes createAssignment(AssignmentDtoReq req) {

        System.out.println("Course ID: " + req.getCourseId());
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() ->
                        new NotFoundException("Course not found with id " + req.getCourseId())
                );

        if (req.getTitle() == null) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        Assignment assignment = assignmentMapper.toEntity(req);
        assignment.setCourse(course);
        assignment.setOrderIndex(assignmentRepository.findMaxOrderIndexByCourse_CourseId(req.getCourseId()) + 1);
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
    public List<AssignmentDtoRes> findByCourseId(Long courseId) {

        List<Assignment> assignments = assignmentRepository.findByCourse_CourseId(courseId);
        return assignments.stream()
                .map(assignmentMapper::toDtoRes)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentDtoRes> findMyAssignments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser_UserIdAndDeletedFalse(userId);
        if (enrollments.isEmpty()) {
            return List.of();
        }

        List<Long> courseIds = enrollments.stream()
                .map(enrollment -> enrollment.getCourse().getCourseId())
                .distinct()
                .collect(Collectors.toList());

        List<Assignment> assignments = assignmentRepository.findByCourse_CourseIdIn(courseIds);
        return assignments.stream()
                .map(assignmentMapper::toDtoRes)
                .collect(Collectors.toList());
    }
}
