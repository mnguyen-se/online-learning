package com.example.online_learning.dto.response;

import com.example.online_learning.entity.Module;
import com.example.online_learning.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDtoRes {

    private Long courseId;
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private User createdBy;

    private User teacher;

    private LocalDateTime createdAt;

    private boolean isPublic;

    private List<ModuleDtoRes> modules;
    
    private List<Long> assignmentIds;
}
