package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.ModuleDtoReq;
import com.example.online_learning.dto.response.ModuleDtoRes;
import com.example.online_learning.entity.Module;
import com.example.online_learning.exception.BadRequestException;
import com.example.online_learning.exception.InvalidOperationException;
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

        if (dto == null) {
            throw new BadRequestException("Module data không được null");
        }

        if (dto.getCourseId() == null) {
            throw new BadRequestException("CourseId không được null");
        }

        Integer maxOrder = moduleRepository.findMaxOrderIndexByCourseId(dto.getCourseId());
        if (maxOrder == null) {
            maxOrder = 0;
        }

        Module module = moduleMapper.toEntity(dto);
        module.setOrderIndex(maxOrder + 1);

        moduleRepository.save(module);
        return moduleMapper.toDto(module);
    }

    @Override
    public void deleteModule(Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found"));

        // Nếu module còn lesson thì không cho xoá
        if (module.getLessons() != null && !module.getLessons().isEmpty()) {
            throw new InvalidOperationException(
                    "Không thể xoá module khi còn lesson"
            );
        }

        moduleRepository.delete(module);
    }

    @Override
    public ModuleDtoRes updateModule(Long moduleId, ModuleDtoReq dto) {

        if (dto == null) {
            throw new BadRequestException("Module data không được null");
        }

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found"));

        boolean isEmptyUpdate =
                dto.getTitle() == null &&
                        dto.getDescription() == null;

        if (isEmptyUpdate) {
            throw new BadRequestException("Không có dữ liệu để update");
        }

        return moduleMapper.updateModule(dto, module);
    }

    @Override
    public List<ModuleDtoRes> findModuleById(Long moduleId) {

        List<Module> modules = moduleRepository.findAllByModuleId(moduleId);

        if (modules.isEmpty()) {
            throw new NotFoundException("Module not found");
        }

        return moduleMapper.toDto(modules);
    }

    @Override
    public List<ModuleDtoRes> findModuleByCourseId(Long courseId) {

        List<Module> modules =
                moduleRepository.findAllByCourse_CourseId(courseId);

        if (modules.isEmpty()) {
            throw new NotFoundException("Course không có module nào");
        }

        return moduleMapper.toDto(modules);
    }
}
