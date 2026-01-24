package com.example.online_learning.repository;

import com.example.online_learning.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByModuleIdIn(List<Long> moduleIds);
    List<Module> findAllByCourse_CourseId(Long courseId);
    List<Module> findAllByIsPublicTrue();
    List<Module> findAllByModuleId(Long moduleIds);
    boolean existsByCourse_CourseIdAndOrderIndex(Long courseId, Integer orderIndex);
    @Query("SELECT COALESCE(MAX(m.orderIndex), 0) FROM Module m WHERE m.course.courseId = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);
}
