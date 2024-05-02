package com.example.demo.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.demo.config.GeminiConfig;



@Service
public class GeminiService {
  	private final RestTemplate restTemplate;
    private final GeminiConfig geminiConfig;

    public GeminiService(RestTemplate restTemplate, GeminiConfig geminiConfig) {
        this.restTemplate = restTemplate;
        this.geminiConfig = geminiConfig;
    }

    public String fetchMessage(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", geminiConfig.getGeminiApiKey());

        String requestJson = """
            {
              "contents": [{
                "parts":[{
                  "text": "%s"
                }]
              }]
            }
        """.formatted(content);
        
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        String gemini_api_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";
        ResponseEntity<String> response = restTemplate.postForEntity(gemini_api_url + geminiConfig.getGeminiApiKey(), entity, String.class);

        return response.getBody();
    }

}