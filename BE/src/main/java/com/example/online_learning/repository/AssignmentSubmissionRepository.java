package com.example.online_learning.repository;

import com.example.online_learning.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AssignmentSubmissionRepository
        extends JpaRepository<AssignmentSubmission, Long> {

    Optional<AssignmentSubmission> findByAssignment_AssignmentIdAndStudent_UserId(
            Long assignmentId, Long userId);

}

