package com.example.online_learning.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;
}

