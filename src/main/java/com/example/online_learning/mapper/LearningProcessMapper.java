package com.example.online_learning.mapper;

import com.example.online_learning.constants.ProgressStatus;
import com.example.online_learning.dto.response.LearningProcessDtoRes;
import com.example.online_learning.entity.LearningProgress;
import org.springframework.stereotype.Component;

@Component
public class LearningProcessMapper {
    public LearningProcessDtoRes toDto(LearningProgress lp){
        int percent = lp.getTotalTasks() == 0
                ? 0
                : (int) Math.round(
                lp.getCompletedTasks() * 100.0 / lp.getTotalTasks()
        );

        return LearningProcessDtoRes.builder()
                .courseId(lp.getCourse().getCourseId())
                .userId(lp.getUser().getUserId())
                .totalTasks(lp.getTotalTasks())
                .completedTasks(lp.getCompletedTasks())
                .remainingTasks(lp.getTotalTasks() - lp.getCompletedTasks())
                .progressPercent(percent)
                .status(lp.getStatus())
                .completed(lp.getStatus() == ProgressStatus.DONE)
                .lastUpdated(lp.getLastUpdated())
                .build();
    }
}
