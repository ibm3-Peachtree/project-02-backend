package com.ruttu.project_02_backend.service.briefing;

import com.ruttu.project_02_backend.dto.briefing.ResponseGeminiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public <T> T generate(
            String prompt, Class<T> clazz, String key) {

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
                        "responseMimeType",
                        clazz == String.class ? "text/plain" : "application/json"  // ✅ String이면 text/plain
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);
        for (int retry = 0; retry < 3; retry++) {

            try {

                ResponseEntity<ResponseGeminiDto> response =
                        restTemplate.postForEntity(
                                url,
                                request,
                                ResponseGeminiDto.class
                        );

                String text = response.getBody()
                        .getCandidates()
                        .getFirst()
                        .getContent()
                        .getParts()
                        .getFirst()
                        .getText();

                T res;
                if (clazz == String.class) {
                    res = clazz.cast(text);
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    res = mapper.readValue(text, clazz);
                }

                // ✅ Redis 저장도 분기
                String toCache = clazz == String.class
                        ? (String) res
                        : new ObjectMapper().writeValueAsString(res);
                redisTemplate.opsForValue().set(key, toCache);

                return res;

            } catch (HttpServerErrorException.ServiceUnavailable e) {

                if (retry == 2) {
                    throw e;
                }

                try {
                    Thread.sleep(1000L * (retry + 1));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ex);
                }
            } catch (Exception e) {
                throw new RuntimeException("Gemini 처리 실패", e);
            }
        }

        throw new RuntimeException("Gemini 호출 실패");


    }
}

