package com.example.online_learning.entity;

import com.example.online_learning.constants.LessonType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "lesson",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"module_id", "order_index"})
        }
)
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
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType lessonType;

    @Column(columnDefinition = "TEXT")
    private String textContent;

    @Column(length = 500,name = "video_url")
    private String videoUrl;


    @Column(nullable = false)
    private Integer orderIndex;

    private boolean isPublic;

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

}
