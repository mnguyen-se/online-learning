package com.example.online_learning.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvDebug {
    public EnvDebug(Environment env) {
        System.out.println("ENV GEMINI_API_KEY = " + env.getProperty("GEMINI_API_KEY"));
    }
}

