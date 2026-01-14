package com.example.online_learning.service;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.entity.CourseModule;

import java.util.List;

public interface ModuleService {
    public ModuleDtoRes findModuleByTitle(String title);
    public void deleteModule(Long moduleId);
    public void updateModule(Long moduleId, ModuleDtoReq dto);
    public void createModule(ModuleDtoReq dto);
    public List<CourseModule> getAllModules();
    public List<ModuleDtoRes> findModulesByCourseId(Long courseId);
    public List<ModuleDtoRes> findModulesByDeletedFalse();

}
