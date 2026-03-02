package com.example.online_learning.service;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;

import java.util.List;

public interface ModuleService {
    ModuleDtoRes createModule(ModuleDtoReq dto);
    void deleteModule(Long moduleId);
    ModuleDtoRes updateModule(Long moduleId, ModuleDtoReq dto);
    ModuleDtoRes findModuleById(Long moduleId);
    List<ModuleDtoRes> findModuleByCourseId(Long courseId);
    List<ModuleDtoRes> findByCourseIdAndIsPublicTrue(Long courseId);
}
