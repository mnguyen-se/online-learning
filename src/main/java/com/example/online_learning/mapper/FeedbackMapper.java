package com.example.online_learning.mapper;

import com.example.online_learning.dto.response.FeedbackDtoRes;
import com.example.online_learning.entity.Feedback;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FeedbackMapper {
    public FeedbackDtoRes toDto(Feedback feedback) {
        FeedbackDtoRes dto = new FeedbackDtoRes();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setGradedContent(feedback.getGradedContent());
        dto.setCourseId(feedback.getCourse().getCourseId());
        dto.setCourseTitle(feedback.getCourse().getTitle());
        dto.setStudentId(feedback.getStudent().getUserId());
        dto.setStudentName(feedback.getStudent().getName());
        dto.setTeacherId(feedback.getTeacher().getUserId());
        dto.setTeacherName(feedback.getTeacher().getName());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }

    public List<FeedbackDtoRes> toDto(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
