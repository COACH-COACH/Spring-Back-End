package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiConfig {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public String getGeminiApiUrl() {
        return geminiApiUrl;
    }
}
