package com.example.online_learning.repository;

import com.example.online_learning.entity.AssignmentCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentCompletionRepository
        extends JpaRepository<AssignmentCompletion, Long> {

    boolean existsByUser_UserIdAndAssignment_AssignmentId(
            Long userId, Long assignmentId
    );
}

