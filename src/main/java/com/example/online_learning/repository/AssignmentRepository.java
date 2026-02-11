package com.example.online_learning.repository;

import com.example.online_learning.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    long countByCourse_CourseId(Long courseId);
    List<Assignment> findByCourse_CourseId(Long courseId);
    @Query("""
SELECT COALESCE(MAX(a.orderIndex), 0)
FROM Assignment a
WHERE a.course.courseId = :courseId
""")
    Integer findMaxOrderIndexByCourse_CourseId(@Param("courseId") Long courseId);

}

