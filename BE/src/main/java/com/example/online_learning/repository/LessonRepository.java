package com.example.online_learning.repository;

import com.example.online_learning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Lesson findByLessonId(Long lessonId);
    List<Lesson> findAllByLessonIdIn(List<Long> lessonIds);
    List<Lesson> findAllByIsPublicTrue();
    @Query("""
    SELECT COUNT(l)
    FROM Lesson l
    WHERE l.module.course.courseId = :courseId
      AND l.isPublic = true
""")
    long countLessonsByCourseId(@Param("courseId") Long courseId);

    @Query("""
    SELECT l.title
    FROM Lesson l
    WHERE l.module.course.courseId = :courseId
      AND l.isPublic = true
      AND l.lessonId NOT IN (
          SELECT lc.lesson.lessonId
          FROM LessonCompletion lc
          WHERE lc.user.userId = :userId
      )
    ORDER BY l.orderIndex ASC
""")
    List<String> findCurrentLessonTitleForPractice(
            @Param("courseId") Long courseId,
            @Param("userId") Long userId
    );

    boolean existsByModule_ModuleIdAndOrderIndex(Long moduleId, Integer orderIndex);
    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Lesson l WHERE l.module.moduleId = :moduleId")
    Integer findMaxOrderIndexByCourseId(@Param("moduleId") Long moduleId);
}
