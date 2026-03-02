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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Caching(evict = {
            @CacheEvict(value = "module:list:course", key = "#dto.courseId"),
            @CacheEvict(value = "course:list:all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "course:list:teacher", allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = "module:detail", key = "#moduleId"),
            @CacheEvict(value = "module:list:course", key = "#dto.courseId"),
            @CacheEvict(value = "course:list:all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "course:list:teacher", allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = "module:detail", key = "#moduleId"),
            @CacheEvict(value = "module:list:course", key = "#dto.courseId"),
            @CacheEvict(value = "course:list:all", allEntries = true),
            @CacheEvict(value = "course:list:public", allEntries = true),
            @CacheEvict(value = "course:list:teacher", allEntries = true)
    })
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
    @Cacheable(value = "module:detail", key = "#moduleId")
    public ModuleDtoRes findModuleById(Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found"));

        return moduleMapper.toDto(module);
    }

    @Override
    @Cacheable(value = "module:list:course", key = "#courseId")
    public List<ModuleDtoRes> findModuleByCourseId(Long courseId) {

        List<Module> modules =
                moduleRepository.findAllByCourse_CourseId(courseId);

        if (modules.isEmpty()) {
            throw new NotFoundException("Course không có module nào");
        }

        return moduleMapper.toDto(modules);
    }

    @Override
    public List<ModuleDtoRes> findByCourseIdAndIsPublicTrue(Long courseId) {
        List<Module> modules = moduleRepository.findAllByCourse_CourseIdAndIsPublicTrue(courseId);
        if(modules.isEmpty()) {
            throw new NotFoundException("Module not found for courseId: " + courseId + " and isPublic: true");
        }
        return moduleMapper.toDto(modules);
    }
}
