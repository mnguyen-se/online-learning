package com.example.online_learning.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.online_learning.constants.LessonType;
import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.dto.response.LessonDtoRes;
import com.example.online_learning.entity.Lesson;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.LessonMapper;
import com.example.online_learning.repository.LessonRepository;
import com.example.online_learning.service.LessonService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final Cloudinary cloudinary;
    public LessonServiceImpl(LessonRepository lessonRepository, LessonMapper lessonMapper, Cloudinary cloudinary) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
        this.cloudinary = cloudinary;
    }
    @Override
    public LessonDtoRes createLesson(LessonDtoReq dto) {
        Lesson l = lessonMapper.toEntity(dto);
        l.setOrderIndex(lessonRepository.findMaxOrderIndexByCourseId(dto.getModuleId()) + 1);
        lessonRepository.save(l);
        return lessonMapper.toDto(l);
    }

    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        lessonRepository.delete(lesson);
    }

    @Override
    public void updateLesson(Long lessonId, LessonDtoReq dto) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        if(lesson == null) throw new NotFoundException("Lesson not found");
        if(dto.getTitle() != null) lesson.setTitle(dto.getTitle());
        if(dto.getTextContent() != null) lesson.setTextContent(dto.getTextContent());
        if(dto.getVideoUrl() != null) lesson.setVideoUrl(dto.getVideoUrl());
        if(dto.getLessonType() != null) lesson.setLessonType(dto.getLessonType());
        lessonRepository.save(lesson);
    }

    @Override
    public List<LessonDtoRes> getAllLessons() {
        return lessonMapper.toDto(lessonRepository.findAll());
    }

    @Override
    public List<LessonDtoRes> findLessonByPublicTrue() {
        List<Lesson> lessons = lessonRepository.findAllByIsPublicTrue();
        if(lessons.isEmpty()) throw new NotFoundException("No lesson found");
        return lessonMapper.toDto(lessons);
    }

    @Override
    public String uploadFile(Long lessonId, MultipartFile file) {

        if (file.getContentType() == null ||
                !file.getContentType().equals("video/mp4")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file MP4");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found"));

        if (lesson.getLessonType() != LessonType.VIDEO) {
            throw new IllegalStateException("Lesson không phải loại VIDEO");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", "lessons"
                    )
            );
            System.out.println(uploadResult);

            String videoUrl = uploadResult.get("secure_url").toString();
            lesson.setVideoUrl(videoUrl);
            lessonRepository.save(lesson);

            return videoUrl;

        } catch (IOException e) {
            throw new RuntimeException("Upload video thất bại", e);
        }
    }



}
