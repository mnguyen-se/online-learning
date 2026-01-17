package com.example.online_learning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDtoReq {

    private Long courseId;      // gắn assignment vào course

    private String title;
    private String description;
    private Integer maxScore;
    private LocalDateTime dueDate;
    private Integer orderIndex;
}

