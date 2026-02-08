package com.example.online_learning.repository;

import com.example.online_learning.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningProcessRepository extends JpaRepository<LearningProgress, Long> {
    Optional<LearningProgress> findByUser_UserIdAndCourse_CourseId(
            Long userId, Long courseId
    );
}
