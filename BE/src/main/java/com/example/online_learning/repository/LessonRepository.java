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
    List<Lesson> findBySection_SectionId(Long sectionId);
    List<Lesson> findAllByDeletedFalse();
    @Query("""
    SELECT COUNT(l)
    FROM Lesson l
    WHERE l.section.module.course.courseId = :courseId
      AND l.isDeleted = false
""")
    long countLessonsByCourseId(@Param("courseId") Long courseId);

    @Query("""
    SELECT l.title
    FROM Lesson l
    WHERE l.section.module.course.courseId = :courseId
      AND l.isDeleted = false
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



}
