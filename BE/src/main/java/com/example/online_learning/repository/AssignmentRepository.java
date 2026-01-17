package com.example.online_learning.repository;

import com.example.online_learning.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    long countByCourse_CourseId(Long courseId);
    List<Assignment> findByCourse_CourseId(Long courseId);
}

