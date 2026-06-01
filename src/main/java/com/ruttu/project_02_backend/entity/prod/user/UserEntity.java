package com.ruttu.project_02_backend.entity.prod.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String email; //이메일
    private String provider; //어떤 계정으로 로그인 했나
    @Column(unique = true)
    private String providerId; //Google이 사용자에게 부여하는 "고유 ID"

    @Column(name = "nickname")
    private String nickname;

    @ColumnDefault("'USER'")
    @Column(name = "role")
    private String role;

    @ColumnDefault("'ACTIVE'")
    @Column(name = "status")
    private String status;

    // 🚍 출발 알림
    @Column(name = "departure_alert", nullable = false)
    @ColumnDefault("0")
    private Boolean departureAlert;

    @Column(name = "departure_minutes")
    private String departureMinutes;

    // 🚇 하차 알림
    @Column(name = "alighting_alert", nullable = false)
    @ColumnDefault("false")
    private Boolean alightingAlert;

    @Column(name = "alighting_mode")
    private String alightingMode;

    @Column(name = "alighting_stops")
    private String alightingStops;

    // 🔊 TTS
    @Column(name = "tts_enabled", nullable = false)
    @ColumnDefault("false")
    private Boolean ttsEnabled;

    @Column(name = "tts_mode")
    private String ttsMode;

    // 📋 브리핑
    @Column(name = "briefing_alert", nullable = false)
    @ColumnDefault("false")
    private Boolean briefingAlert;

    @Column(name = "morning_time")
    private String morningTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;


}