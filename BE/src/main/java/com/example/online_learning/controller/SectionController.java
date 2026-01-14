package com.example.online_learning.controller;

import com.example.online_learning.dto.request.SectionDtoReq;
import com.example.online_learning.service.SectionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sections")
public class SectionController {
    private final SectionService sectionService;
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/")
    public Object getAllSections(){
        return sectionService.getAllSections();
    }

    @PostMapping("/create")
    public Object createSection(@RequestBody SectionDtoReq dto){
        return sectionService.createSection(dto);
    }
}
