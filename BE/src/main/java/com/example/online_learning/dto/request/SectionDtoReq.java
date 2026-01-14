package com.example.online_learning.dto.request;

import com.example.online_learning.entity.CourseModule;
import com.example.online_learning.entity.Lesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDtoReq {

    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;

}
