package com.example.online_learning.servivceImpl;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.entity.CourseModule;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.ModuleMapper;
import com.example.online_learning.repository.ModuleRepository;
import com.example.online_learning.service.ModuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    public ModuleServiceImpl(ModuleRepository moduleRepository, ModuleMapper moduleMapper) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
    }

    @Override
    public ModuleDtoRes findModuleByTitle(String title) {
        CourseModule module = moduleRepository.findByTitle(title);
        return moduleMapper.toDto(module);
    }

    @Override
    public void deleteModule(Long moduleId) {
        CourseModule module = moduleRepository.findById(moduleId).orElse(null);
        if(module == null) throw new NotFoundException("Module not found");
        module.setDeleted(true);
        moduleRepository.save(module);
    }

    @Override
    public void updateModule(Long moduleId, ModuleDtoReq dto) {
        CourseModule module = moduleRepository.findById(moduleId).orElse(null);
        if(module == null) throw new NotFoundException("Module not found");
        moduleRepository.save(moduleMapper.toEntity(dto));
    }

    @Override
    public void createModule(ModuleDtoReq dto) {
        CourseModule module = moduleMapper.toEntity(dto);
        moduleRepository.save(module);
    }

    @Override
    public List<CourseModule> getAllModules() {
        return moduleRepository.findAll();
    }

    @Override
    public List<ModuleDtoRes> findModulesByCourseId(Long courseId) {
        List<CourseModule> modules = moduleRepository.findByCourse_CourseId(courseId);
        return moduleMapper.toDto(modules);
    }

    @Override
    public List<ModuleDtoRes> findModulesByDeletedFalse() {
        List<CourseModule> modules = moduleRepository.findAllByDeletedFalse();
        if(modules.isEmpty()) throw new NotFoundException("No module found");
        return moduleMapper.toDto(modules);
    }
}
