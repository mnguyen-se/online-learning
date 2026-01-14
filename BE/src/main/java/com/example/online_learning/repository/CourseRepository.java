package com.example.online_learning.repository;

import com.example.online_learning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByCourseId(Long courseId);
    Course findByTitle(String title);
    List<Course> findByDeletedFalse();
}
