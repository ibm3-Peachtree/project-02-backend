package com.ruttu.project_02_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // Kakao API 호출용 WebClient Bean 등록
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .build();
    }
}
