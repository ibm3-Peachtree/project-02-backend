package com.ruttu.project_02_backend.controller.push;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.service.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class PushController {

    private final PushService pushService;

    @PostMapping("/test")
    public String sendTestPush(
            @RequestParam String token
    ) throws FirebaseMessagingException {

        pushService.sendPush(
                token,
                "테스트 알림",
                "푸시가 정상적으로 동작합니다."
        );

        return "success";
    }

}