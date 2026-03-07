package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatisticsDtoRes {
    private Long courseId;
    private String courseTitle;
    private Long activeStudents;
    private Long totalSubmissions;
    private Long ungradedSubmissions;
    private List<WeeklySubmissionDto> weeklySubmissions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklySubmissionDto {
        private String dayOfWeek;
        private Integer count;
    }
}
