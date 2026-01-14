package com.example.online_learning.repository;

import com.example.online_learning.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<CourseModule, Long> {
    CourseModule findByTitle(String title);
    List<CourseModule> findAllByDeletedFalse();
    List<CourseModule> findByCourse_CourseId(Long courseId);
}
