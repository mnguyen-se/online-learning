package com.example.online_learning.repository;

import com.example.online_learning.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUser_UserIdAndCourse_CourseIdAndDeletedFalse(
            Long userId, Long courseId
    );
    
    Optional<Enrollment> findByUser_UserNameAndCourse_CourseIdAndDeletedFalse(
            String username, Long courseId
    );
    
    Optional<Enrollment> findByUser_UserIdAndCourse_CourseId(
            Long userId, Long courseId
    );
    
    List<Enrollment> findByCourse_CourseIdAndDeletedFalse(Long courseId);
    
    List<Enrollment> findByUser_UserIdAndDeletedFalse(Long userId);
    
    List<Enrollment> findByUser_UserNameAndDeletedFalse(String username);
}
