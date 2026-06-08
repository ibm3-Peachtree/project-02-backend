package com.ruttu.project_02_backend.entity.prod.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalTime;

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

    // 앱 푸시 토큰
    @Column(name = "app_push_token")
    private String appPushToken;

    // -- 알림 설정 프론트하고 맞춰서 이름 변경 --
    // 🚍 출발 알림
    @Column(name = "departure_alert", nullable = false)
    @ColumnDefault("0")
    private Boolean departureAlert; // ON/OFF

    @Column(name = "departure_minutes")
    private String departureMinutes; // "5분 전", "10분 전", "15분 전", "30분 전"

    // 🚇 하차 알림
    @Column(name = "alighting_alert", nullable = false)
    @ColumnDefault("false")
    private Boolean alightingAlert; // ON/OFF

    @Column(name = "alighting_mode")
    private String alightingMode; // "진동", "소리", "진동+소리"

    @Column(name = "alighting_stops")
    private String alightingStops; // "1정류장 전", "2정류장 전", "3정류장 전"

    // 🔊 TTS
    @Column(name = "tts_enabled", nullable = false)
    @ColumnDefault("false")
    private Boolean ttsEnabled; // ON/OFF

    @Column(name = "tts_mode")
    private String ttsMode; // "매 단계마다", "환승 시에만", "출발·도착만"

    // 📋 브리핑
    @Column(name = "briefing_alert", nullable = false)
    @ColumnDefault("false")
    private Boolean briefingAlert; // ON/OFF

    @Column(name = "morning_time")
    private LocalTime morningTime; // 시간 설정
    // -- 알림 설정 프론트하고 맞춰서 이름 변경 --

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;


}