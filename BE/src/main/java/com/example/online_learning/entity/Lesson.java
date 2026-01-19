package com.example.online_learning.entity;

import com.example.online_learning.constants.LessonType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private CourseSection section;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType lessonType;

    @Column(columnDefinition = "TEXT")
    private String contentUrl;

    private Integer duration;

    @Column(nullable = false)
    private Integer orderIndex;

    private boolean isDeleted;
}
