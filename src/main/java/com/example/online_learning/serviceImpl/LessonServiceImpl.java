package com.example.online_learning.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.online_learning.constants.LessonType;
import com.example.online_learning.dto.request.LessonDtoReq;
import com.example.online_learning.dto.response.LessonDtoRes;
import com.example.online_learning.entity.Lesson;
import com.example.online_learning.exception.BadRequestException;
import com.example.online_learning.exception.FileUploadException;
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

    public LessonServiceImpl(
            LessonRepository lessonRepository,
            LessonMapper lessonMapper,
            Cloudinary cloudinary
    ) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
        this.cloudinary = cloudinary;
    }

    // ================= CREATE =================
    @Override
    public LessonDtoRes createLesson(LessonDtoReq dto) {

        if (dto == null) {
            throw new BadRequestException("Lesson data không được để trống");
        }

        Lesson lesson = lessonMapper.toEntity(dto);

        Integer maxOrder = lessonRepository
                .findMaxOrderIndexByCourseId(dto.getModuleId());

        lesson.setOrderIndex(
                maxOrder == null ? 1 : maxOrder + 1
        );

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    // ================= DELETE =================
    @Override
    public void deleteLesson(Long lessonId) {

        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        if (lesson == null) {
            throw new NotFoundException("Lesson không tồn tại");
        }

        lessonRepository.delete(lesson);
    }

    // ================= UPDATE =================
    @Override
    public void updateLesson(Long lessonId, LessonDtoReq dto) {

        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        if (lesson == null) {
            throw new NotFoundException("Lesson không tồn tại");
        }

        if (dto == null) {
            throw new BadRequestException("Dữ liệu update không hợp lệ");
        }

        if (dto.getTitle() != null) {
            lesson.setTitle(dto.getTitle());
        }

        if (dto.getTextContent() != null) {
            lesson.setTextContent(dto.getTextContent());
        }

        if (dto.getVideoUrl() != null) {
            lesson.setVideoUrl(dto.getVideoUrl());
        }

        if (dto.getLessonType() != null) {
            lesson.setLessonType(dto.getLessonType());
        }

        lessonRepository.save(lesson);
    }

    // ================= GET ALL =================
    @Override
    public List<LessonDtoRes> getAllLessons() {

        List<Lesson> lessons = lessonRepository.findAll();
        if (lessons.isEmpty()) {
            throw new NotFoundException("Không có lesson nào");
        }

        return lessonMapper.toDto(lessons);
    }

    // ================= GET PUBLIC =================
    @Override
    public List<LessonDtoRes> findLessonByPublicTrue() {

        List<Lesson> lessons = lessonRepository.findAllByIsPublicTrue();
        if (lessons.isEmpty()) {
            throw new NotFoundException("Không có lesson public nào");
        }

        return lessonMapper.toDto(lessons);
    }

    // ================= UPLOAD VIDEO =================
    @Override
    public String uploadFile(Long lessonId, MultipartFile file) {

        // 1️⃣ Validate file
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File upload không được để trống");
        }

        if (file.getContentType() == null ||
                !file.getContentType().equalsIgnoreCase("video/mp4")) {
            throw new BadRequestException("Chỉ chấp nhận file MP4");
        }

        // 2️⃣ Check lesson
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson không tồn tại"));

        if (lesson.getLessonType() != LessonType.VIDEO) {
            throw new BadRequestException("Lesson này không phải loại VIDEO");
        }

        // 3️⃣ Upload Cloudinary
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", "lessons"
                    )
            );

            String videoUrl = uploadResult.get("secure_url").toString();

            lesson.setVideoUrl(videoUrl);
            lessonRepository.save(lesson);

            return videoUrl;

        } catch (IOException e) {
            throw new FileUploadException("Upload video thất bại");
        }
    }
}
