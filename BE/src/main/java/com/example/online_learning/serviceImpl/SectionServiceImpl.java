package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.SectionDtoReq;
import com.example.online_learning.entity.CourseSection;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.SectionMapper;
import com.example.online_learning.repository.SectionRepository;
import com.example.online_learning.service.SectionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;
    public SectionServiceImpl(SectionRepository sectionRepository, SectionMapper sectionMapper) {
        this.sectionRepository = sectionRepository;
        this.sectionMapper = sectionMapper;
    }
    @Override
    public List<CourseSection> getAllSections() {
        List<CourseSection> sections = sectionRepository.findAll();
        if(sections.isEmpty()) throw new NotFoundException("No section found");
        return sections;
    }

    @Override
    public CourseSection createSection(SectionDtoReq dto) {
        CourseSection section = sectionMapper.toEntity(dto);
        return sectionRepository.save(section);
    }
}
