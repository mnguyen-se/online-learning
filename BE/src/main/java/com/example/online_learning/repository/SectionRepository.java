package com.example.online_learning.repository;

import com.example.online_learning.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<CourseSection, Long> {
    CourseSection findByTitle(String sectionTitle);
}
