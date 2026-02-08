package com.example.online_learning.repository;

import com.example.online_learning.entity.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonCompletionRepository
        extends JpaRepository<LessonCompletion, Long> {

    boolean existsByUser_UserIdAndLesson_LessonId(
            Long userId, Long lessonId
    );
}

