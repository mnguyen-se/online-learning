package com.example.online_learning.serviceImpl;

import com.example.online_learning.entity.Lesson;
import com.example.online_learning.entity.LessonCompletion;
import com.example.online_learning.repository.LessonCompletionRepository;
import com.example.online_learning.repository.LessonRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.LearningProcessService;
import com.example.online_learning.service.LessonCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonCompletionServiceImpl
        implements LessonCompletionService {

    private final LessonCompletionRepository completionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LearningProcessService learningProcessService;

    @Override
    public void completeLesson(Long lessonId, Long userId) {

        if (completionRepository
                .existsByUser_UserIdAndLesson_LessonId(
                        userId, lessonId)) {
            return;
        }

        Lesson lesson =
                lessonRepository.findById(lessonId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Lesson not found")
                        );

        LessonCompletion completion = LessonCompletion.builder()
                .user(userRepository.getReferenceById(userId))
                .lesson(lesson)
                .completedAt(LocalDateTime.now())
                .build();

        completionRepository.save(completion);

        // 🔥 CỘNG PROGRESS
        learningProcessService.increaseProgress(
                lesson
                        .getCourse()
                        .getCourseId(),
                userId
        );
    }
}

