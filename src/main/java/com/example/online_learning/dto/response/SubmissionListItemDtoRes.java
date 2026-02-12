package com.example.online_learning.dto.response;

import com.example.online_learning.constants.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionListItemDtoRes {
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Integer score;
    private Integer maxScore;
    private SubmissionStatus status;
    private LocalDateTime submittedAt;
}
