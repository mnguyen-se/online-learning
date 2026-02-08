package com.example.online_learning.serviceImpl;

import com.example.online_learning.config.GeminiConfig;
import com.example.online_learning.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiServiceImpl implements GeminiService {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiServiceImpl(GeminiConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
    }

    @Override
    public String generateContent(String prompt) {

        if (geminiConfig.getApiKey() == null) {
            throw new IllegalStateException("GEMINI_API_KEY is not loaded");
        }

        String url = geminiConfig.getBaseUrl()
                + "/" + geminiConfig.getModel()
                + ":generateContent"
                + "?key=" + geminiConfig.getApiKey();

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }
}

