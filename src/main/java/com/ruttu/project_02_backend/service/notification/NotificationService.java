package com.ruttu.project_02_backend.service.notification;

import com.ruttu.project_02_backend.dto.notification.NotificationDto;
import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
import com.ruttu.project_02_backend.exception.user.UserNotFoundException;
import com.ruttu.project_02_backend.repository.prod.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserRepository userRepository;
    //알림 조회
    public NotificationDto getSetting(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return NotificationDto.builder() // default 값 세팅
                // 출발 알림
                .departureAlert(Boolean.TRUE.equals(user.getDepartureAlert()))
                .departureMinutes(
                        user.getDepartureMinutes() != null
                                ? user.getDepartureMinutes()
                                : "10분 전"
                )
                // 하차 알림
                .alightingAlert(Boolean.TRUE.equals(user.getAlightingAlert()))
                .alightingMode(
                        user.getAlightingMode() != null
                                ? user.getAlightingMode()
                                : "진동"
                )

                .alightingStops(
                        user.getAlightingStops() != null
                                ? user.getAlightingStops()
                                : "2정류장 전"
                )
                // TTS
                .ttsEnabled(Boolean.TRUE.equals(user.getTtsEnabled()))
                .ttsMode(
                        user.getTtsMode() != null
                                ? user.getTtsMode()
                                : "매 단계마다"
                )
                // 브리핑
                .briefingAlert(Boolean.TRUE.equals(user.getBriefingAlert()))
                .morningTime(
                        user.getMorningTime() != null
                                ? user.getMorningTime()
                                : LocalTime.of(7, 30)
                )
                .build();
    }

    @Transactional
    //알림 설정
    public void updateNotification(
            Long userId,
            NotificationDto notificationDto
    ) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow();
        // 출발 알림
        user.setDepartureAlert(notificationDto.isDepartureAlert());
        user.setDepartureMinutes(notificationDto.getDepartureMinutes());
        //하차 알림
        user.setAlightingAlert(notificationDto.isAlightingAlert());
        user.setAlightingMode(notificationDto.getAlightingMode());
        user.setAlightingStops(notificationDto.getAlightingStops());
        // TTS
        user.setTtsEnabled(notificationDto.isTtsEnabled());
        user.setTtsMode(notificationDto.getTtsMode());
        // 브리핑
        user.setBriefingAlert(notificationDto.isBriefingAlert());
        user.setMorningTime(notificationDto.getMorningTime());
    }
}
