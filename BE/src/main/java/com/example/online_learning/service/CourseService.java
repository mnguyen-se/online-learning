package com.example.online_learning.service;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.entity.Course;
import com.example.online_learning.security.CustomUserDetail;

import java.util.List;

public interface CourseService {
    public void createCourse(CourseDtoReq dto, CustomUserDetail userDetail);
    public void deleteCourse(Long courseId);
    public void updateCourse(Long courseId, CourseDtoReq dto);
    public List<Course> getAllCourses();
    public List<CourseDtoRes> findCoursesByPublicTrue();
}
