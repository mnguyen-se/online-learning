package com.example.online_learning.dto.request;

import com.example.online_learning.constants.LessonType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDtoReq {
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType lessonType;

    @Column(columnDefinition = "TEXT")
    private String contentUrl;

    @Column(nullable = false)
    private Integer orderIndex;

    private Long moduleId;

}
