package com.example.online_learning.dto.response;

import com.example.online_learning.constants.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LearningProcessDtoRes {
    private Long courseId;
    private Long userId;

    private int totalTasks;
    private int completedTasks;
    private int remainingTasks;

    private double progressPercent;

    private ProgressStatus status;
    private boolean completed;

    private LocalDateTime lastUpdated;
}
