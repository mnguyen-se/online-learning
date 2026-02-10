package com.example.online_learning.service;

import com.example.online_learning.dto.request.CourseDtoReq;
import com.example.online_learning.dto.request.UpdateCourseDtoReq;
import com.example.online_learning.dto.response.CourseDtoRes;
import com.example.online_learning.security.CustomUserDetail;

import java.util.List;

public interface CourseService {
    public CourseDtoRes createCourse(CourseDtoReq dto, CustomUserDetail userDetail);
    public CourseDtoRes updateCourse(Long courseId, UpdateCourseDtoReq dto, CustomUserDetail userDetail);
    public List<CourseDtoRes> getAllCourses();
    public List<CourseDtoRes> findCoursesByPublicTrue();
    public List<CourseDtoRes> getMyCourses(CustomUserDetail userDetail);
}
