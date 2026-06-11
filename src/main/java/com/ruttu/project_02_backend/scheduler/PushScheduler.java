package com.ruttu.project_02_backend.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ruttu.project_02_backend.service.briefing.BriefingService;
import com.ruttu.project_02_backend.service.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushScheduler {

    private final PushService pushService;

    @Scheduled(cron = "0 */5 * * * *")  // 매 5분마다
    public void sendDepartureNotifications() throws FirebaseMessagingException {

        // 5분 전
        pushService.sendDepartureNotification(5);

        // 10분 전
        pushService.sendDepartureNotification(10);

        // 15분 전
        pushService.sendDepartureNotification(15);

        // 30분 전
        pushService.sendDepartureNotification(30);
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void sendBriefingNotifications() throws FirebaseMessagingException{
        pushService.sendBriefingNotifications();
    }
}
