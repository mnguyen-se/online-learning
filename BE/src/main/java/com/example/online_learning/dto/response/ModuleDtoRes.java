package com.example.online_learning.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDtoRes {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;

    private String courseTitle;
}
