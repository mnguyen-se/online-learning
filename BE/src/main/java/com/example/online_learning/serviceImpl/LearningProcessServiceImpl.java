package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.ProgressStatus;
import com.example.online_learning.dto.response.LearningProcessDtoRes;
import com.example.online_learning.entity.LearningProgress;
import com.example.online_learning.repository.*;
import com.example.online_learning.service.LearningProcessService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LearningProcessServiceImpl implements LearningProcessService {

    private final LearningProcessRepository learningProgressRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /**
     * Tạo learning process khi user enroll course
     */
    @Override
    public void createLearningProcess(Long courseId, Long userId) {

        if (learningProgressRepository
                .findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .isPresent()) {
            return;
        }

        LearningProgress progress = LearningProgress.builder()
                .user(userRepository.getReferenceById(userId))
                .course(courseRepository.getReferenceById(courseId))
                .completedTasks(0)
                .status(ProgressStatus.IN_PROGRESS)
                .build();

        learningProgressRepository.save(progress);
    }


    /**
     * +1 progress khi hoàn thành lesson / assignment
     */
    @Override
    public void increaseProgress(Long courseId, Long userId) {

        LearningProgress progress = learningProgressRepository
                .findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .orElseThrow(() ->
                        new IllegalStateException("Learning progress not found")
                );

        progress.setCompletedTasks(progress.getCompletedTasks() + 1);

        learningProgressRepository.save(progress);
    }


    /**
     * FE lấy progress để hiển thị
     */
    @Override
    @Transactional(readOnly = true)
    public LearningProcessDtoRes getByCourseAndUser(Long courseId, Long userId) {

        LearningProgress progress = learningProgressRepository
                .findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .orElseThrow(() ->
                        new IllegalStateException("Learning progress not found")
                );

        int totalTasks =
                (int) lessonRepository.countLessonsByCourseId(courseId)
                        + (int) assignmentRepository.countByCourse_CourseId(courseId);

        int completedTasks = progress.getCompletedTasks();

        int percent = totalTasks == 0
                ? 0
                : (int) Math.round(completedTasks * 100.0 / totalTasks);

        ProgressStatus status =
                completedTasks >= totalTasks && totalTasks > 0
                        ? ProgressStatus.DONE
                        : ProgressStatus.IN_PROGRESS;

        return LearningProcessDtoRes.builder()
                .courseId(courseId)
                .userId(userId)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .remainingTasks(totalTasks - completedTasks)
                .progressPercent(percent)
                .status(status)
                .completed(status == ProgressStatus.DONE)
                .lastUpdated(progress.getLastUpdated())
                .build();
    }


    /**
     * Check course đã hoàn thành chưa
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isCourseCompleted(Long courseId, Long userId) {

        return learningProgressRepository
                .findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .map(lp -> lp.getStatus() == ProgressStatus.DONE)
                .orElse(false);
    }
}

