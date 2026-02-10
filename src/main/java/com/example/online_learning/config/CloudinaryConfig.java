package com.example.online_learning.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;
@Profile("!test")
@Configuration
public class CloudinaryConfig {
    @Value("${spring.cloudinary.cloud-name}")
    private String cloudName;

    @Value("${spring.cloudinary.api-key}")
    private String apiKey;

    @Value("${spring.cloudinary.api-secret}")
    private String apiSecret;



    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);

        return new Cloudinary(config);
    }
}

