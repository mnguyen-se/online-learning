package com.example.online_learning.service;

import com.example.online_learning.dto.request.SectionDtoReq;
import com.example.online_learning.entity.CourseSection;

import java.util.List;

public interface SectionService {
    public List<CourseSection> getAllSections();
    public CourseSection createSection(SectionDtoReq dto);
}
