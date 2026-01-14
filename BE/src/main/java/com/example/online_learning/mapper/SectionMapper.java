package com.example.online_learning.mapper;

import com.example.online_learning.dto.request.SectionDtoReq;
import com.example.online_learning.entity.CourseModule;
import com.example.online_learning.entity.CourseSection;
import com.example.online_learning.repository.ModuleRepository;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper {
    private final ModuleRepository moduleRepository;
    public SectionMapper(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }
    public CourseSection toEntity(SectionDtoReq dto){
        CourseSection section = new CourseSection();
        section.setTitle(dto.getTitle());
        section.setOrderIndex(dto.getOrderIndex());
        CourseModule module = moduleRepository.findById(dto.getModuleId()).orElse(null);
        section.setModule(module);
        return section;
    }
}
