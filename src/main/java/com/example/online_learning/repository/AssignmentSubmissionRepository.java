package com.example.online_learning.repository;

import com.example.online_learning.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AssignmentSubmissionRepository
        extends JpaRepository<AssignmentSubmission, Long> {

    Optional<AssignmentSubmission> findByAssignment_AssignmentIdAndStudent_UserId(
            Long assignmentId, Long userId);

    List<AssignmentSubmission> findByAssignment_AssignmentId(Long assignmentId);

    @Query("""
SELECT COUNT(s)
FROM AssignmentSubmission s
WHERE s.student.userId = :userId
AND s.assignment.course.courseId = :courseId
AND s.status = com.example.online_learning.constants.SubmissionStatus.COMPLETED
""")
    long countCompletedAssignmentsByUserAndCourse(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId
    );

}

