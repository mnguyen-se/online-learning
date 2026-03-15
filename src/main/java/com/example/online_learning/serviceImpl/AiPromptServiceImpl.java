package com.example.online_learning.serviceImpl;

import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.AssignmentSubmission;
import com.example.online_learning.entity.Feedback;
import com.example.online_learning.entity.User;
import com.example.online_learning.repository.AssignmentSubmissionRepository;
import com.example.online_learning.repository.FeedbackRepository;
import com.example.online_learning.repository.LessonRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.service.AiPromptService;
import com.example.online_learning.service.GeminiService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AiPromptServiceImpl implements AiPromptService {
    private final AssignmentSubmissionRepository submissionRepo;
    private final FeedbackRepository feedbackRepo;
    private final GeminiService geminiService;
    private final LessonRepository lessonRepo;
    private final UserDetailServiceImpl userDetailService;

    public AiPromptServiceImpl(AssignmentSubmissionRepository submissionRepo,
                               FeedbackRepository feedbackRepo,
                               GeminiService geminiService,
                               LessonRepository lessonRepo,
                               UserDetailServiceImpl userDetailService) {
        this.submissionRepo = submissionRepo;
        this.feedbackRepo = feedbackRepo;
        this.geminiService = geminiService;
        this.lessonRepo = lessonRepo;
        this.userDetailService = userDetailService;
    }

    @Override
    public Feedback generateFeedbackPrompt(Long submissionId, CustomUserDetail userDetail) {
        AssignmentSubmission sub = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Assignment a = sub.getAssignment();

        String prompt = """
                You are a helpful teacher.
                Give constructive feedback for the student's submission.

                Assignment: %s
                Description: %s

                Student submission:
                %s

                Give feedback in Vietnamese, friendly and concise.
                """.formatted(
                a.getTitle(),
                a.getDescription(),
                sub.getContent()
        );

        String rawJson = geminiService.generateContent(prompt);
        String commentText = extractTextFromGeminiJson(rawJson);

        Feedback feedback = Feedback.builder()
                .submission(sub)
                .comment(commentText) // lưu text thực, không còn escape
                .createdAt(LocalDateTime.now())
                .teacher(userDetail.getUser())
                .build();

        return feedbackRepo.save(feedback);
    }

    @Override
    public String generateHintLessonPrompt(Long lessonId) {
        var lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        String prompt = """
                You are a friendly tutor.
                Help the student understand the lesson in a simple and clear way.

                Lesson title: %s
                Lesson type: %s

                Lesson content:
                %s

                Requirements:
                - Explain in Vietnamese
                - Use simple words
                - Give examples if possible
                - Keep it concise
                """.formatted(
                lesson.getTitle(),
                lesson.getLessonType(),
                lesson.getTextContent()
        );

        String rawJson = geminiService.generateContent(prompt);
        return extractTextFromGeminiJson(rawJson);
    }

    @Override
    public String generateQuizPracticePrompt(Long lessonId) {
        var lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        String prompt = """
                Create a standalone HTML quiz for students to practice.

                Lesson title: %s

                Requirements:
                - 10 to 20 true/false questions
                - Language: Vietnamese
                - Each question has radio buttons labeled "Đúng" and "Sai" ONLY
                - DO NOT show the English words "True" or "False" anywhere
                - Show result after submitting
                - Show result of each question below the question
                - Include inline CSS and JavaScript
                - Return ONLY valid HTML code, NO markdown, NO ``` block
                """.formatted(lesson.getTextContent());

        String rawJson = geminiService.generateContent(prompt);
        return extractTextFromGeminiJson(rawJson); // trả về HTML sạch
    }

    /**
     * Helper: trích text thực từ JSON trả về của Gemini
     */
    private String extractTextFromGeminiJson(String geminiJson) {
        try {
            com.fasterxml.jackson.databind.JsonNode root =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(geminiJson);

            // Lấy phần text trong candidates[0].content.parts[0].text
            return root.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini JSON", e);
        }
    }
}
