package com.example.online_learning.repository;

import com.example.online_learning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByAssignment_AssignmentIdOrderByOrderIndexAsc(Long assignmentId);
    
    void deleteByAssignment_AssignmentId(Long assignmentId);
}
