package com.ruttu.project_02_backend.service.briefing;

import com.ruttu.project_02_backend.dto.briefing.GeminiResultDto;
import com.ruttu.project_02_backend.dto.briefing.ResponseGeminiDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public GeminiResultDto generate(String prompt) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        Map<String, Object> body = Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<ResponseGeminiDto> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        ResponseGeminiDto.class
                );
        String text= response.getBody()
                .getCandidates()
                .getFirst()
                .getContent()
                .getParts()
                .getFirst()
                .getText();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(text, GeminiResultDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Gemini 응답 JSON 파싱 실패\n" + text, e);
        }
    }
}
