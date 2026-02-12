package com.example.online_learning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeSubmissionDtoReq {
    private Integer score;
    private Boolean requestRevision;
    private String comment;
}
