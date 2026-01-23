package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.entity.Module;
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
    public ModuleDtoRes createModule(ModuleDtoReq dto) {
        if(moduleRepository.existsByCourse_CourseIdAndOrderIndex(
                dto.getCourseId(), dto.getOrderIndex()
        )) throw new IllegalArgumentException("Order index already exists in the course");
        Module module = moduleMapper.toEntity(dto);
        moduleRepository.save(module);
        return moduleMapper.toDto(module);
    }

    @Override
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId).orElseThrow(()
                -> new NotFoundException("Module not found"));
        moduleRepository.delete(module);
    }

    @Override
    public ModuleDtoRes updateModule(Long moduleId, ModuleDtoReq dto) {
        Module module = moduleRepository.findById(moduleId).orElseThrow(()
                -> new NotFoundException("Module not found"));
        return moduleMapper.updateModule(dto, module);
    }

    @Override
    public List<ModuleDtoRes> findModuleById(Long moduleId) {
        List<Module> modules = moduleRepository.findAllByModuleId(moduleId);
        return moduleMapper.toDto(modules);
    }

    @Override
    public List<ModuleDtoRes> findModuleByCourseId(Long courseId) {
        List<Module> modules = moduleRepository.findAllByCourse_CourseId(courseId);
        return moduleMapper.toDto(modules);
    }
}
