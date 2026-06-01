package com.ruttu.project_02_backend.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    // 🚍 출발 알림
    private boolean departureAlert;
    private String departureMinutes;   // "5분 전", "10분 전", "15분 전", "30분 전"

    // 🚇 하차 알림
    private boolean alightingAlert;
    private String alightingMode;      // "진동", "소리", "진동+소리"
    private String alightingStops;     // "1정류장 전", "2정류장 전", "3정류장 전"

    // 🔊 TTS
    private boolean ttsEnabled;
    private String ttsMode;            // "매 단계마다", "환승 시에만", "출발·도착만"

    // 📋 브리핑
    private boolean briefingAlert;
    private String morningTime;        // "07:30"
}
