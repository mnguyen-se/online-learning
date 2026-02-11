package com.example.online_learning.repository;

import com.example.online_learning.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningProcessRepository extends JpaRepository<LearningProgress, Long> {
    Optional<LearningProgress> findByUser_UserIdAndCourse_CourseId(
            Long userId, Long courseId
    );
    @Modifying(clearAutomatically = true)
    @Query("""
UPDATE LearningProgress lp
SET lp.completedTasks = lp.completedTasks + 1,
    lp.progressPercent =
    ((lp.completedTasks + 1) * 100.0 / lp.totalTasks)
WHERE lp.user.userId = :userId
AND lp.course.courseId = :courseId
""")
    int increaseProgress(@Param("userId") Long userId,
                         @Param("courseId") Long courseId);


}
