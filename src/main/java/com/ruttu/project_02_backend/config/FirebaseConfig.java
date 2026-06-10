package com.ruttu.project_02_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config}")
    private String firebaseConfig;

    @PostConstruct
    public void init() throws IOException {

        InputStream inputStream =
                new ByteArrayInputStream(
                        firebaseConfig.getBytes(StandardCharsets.UTF_8)
                );

        FirebaseOptions options =
                FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials.fromStream(inputStream)
                        )
                        .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}