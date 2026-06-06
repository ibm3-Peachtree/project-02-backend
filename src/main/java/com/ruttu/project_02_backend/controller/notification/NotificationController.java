package com.ruttu.project_02_backend.controller.notification;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.notification.NotificationDto;
import com.ruttu.project_02_backend.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "Notification API", description = "알림 관리 API")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @Operation(
            summary = "알림 설정 조회",
            description = "출발 권장 알림, 하차 알림, TTS 안내, 브리핑 알림 조회"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<NotificationDto> getNotificationSetting(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();

        NotificationDto response = notificationService.getSetting(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(
            summary = "알림 설정",
            description = "출발 권장 알림, 하차 알림, TTS 안내, 브리핑 알림 설정"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Map<String, String>>updateNotification(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody NotificationDto notificationDto
    ) {

        notificationService.updateNotification(
                user.getUserId(),
                notificationDto
        );

        return ResponseEntity.ok(
                Map.of("message", "알림 설정이 저장되었습니다.")
        );
    }
}
