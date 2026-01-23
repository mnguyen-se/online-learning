package com.example.online_learning.dto.response;

import com.example.online_learning.entity.Course;
import com.example.online_learning.entity.Lesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModuleDtoRes {
    private Long courseId;
    private List<LessonDtoRes> lessons;
    private String title;
    private Integer orderIndex;
}
