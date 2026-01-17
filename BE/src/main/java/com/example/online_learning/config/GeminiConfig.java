package com.example.online_learning.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini")
@Data
@Getter
@Setter
public class GeminiConfig {

    private String apiKey;
    private String model;
    private String baseUrl;
    private int maxTokens;
    private double temperature;

    @PostConstruct
    public void check() {
        System.out.println("Gemini API key loaded = " + apiKey);
    }
}



