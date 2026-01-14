package com.example.online_learning.repository;

import com.example.online_learning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Lesson findByLessonId(Long lessonId);
    List<Lesson> findBySection_SectionId(Long sectionId);
    List<Lesson> findAllByDeletedFalse();

}
