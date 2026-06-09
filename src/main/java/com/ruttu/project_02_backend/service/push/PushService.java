package com.ruttu.project_02_backend.service.push;

import com.google.firebase.messaging.*;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
import com.ruttu.project_02_backend.exception.user.UserNotFoundException;
import com.ruttu.project_02_backend.repository.prod.routine.UserRoutineRepository;
import com.ruttu.project_02_backend.repository.prod.user.UserRepository;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushService {

    private final LiveRouteService liveRouteService;

    private final UserRoutineRepository userRoutineRepository;
    private final UserRepository userRepository;

    private final RedisTemplate<String, Object> redisTemplate;


    public void sendBriefingNotifications() throws FirebaseMessagingException{
        LocalTime now = LocalTime.now();
        LocalDateTime today = LocalDateTime.now();
        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        List<UserEntity> users = userRepository.findAllByMorningTime(now);

        for (UserEntity user : users){
            Long routineId = liveRouteService.getTodayRoutine(user.getId())
                    .getId();
            try {
                String contents = (String) redisTemplate.opsForValue()
                                .get("briefing:" + ":" + routineId + ":" + date);
                sendPush(
                        user.getAppPushToken(),
                        "오늘의 브리핑",
                        contents,
                        "channel_vibrate"
                );
            } catch (IllegalStateException e) {

            }
        }
    }


    public void sendGetOffNotification(Long userId, int remainingStations) throws FirebaseMessagingException{
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getAlightingStops().contains(String.valueOf(remainingStations)))
            sendPush(
                    user.getAppPushToken(),
                    "하차 알림",
                    user.getAlightingStops() + "입니다.",
                     getMode(user.getAlightingMode())
            );
    }

    public void sendDepartureNotification(int targetTime) throws FirebaseMessagingException {

        LocalTime now = LocalTime.now();

        LocalTime fiveMinutesAgo = now.minusMinutes(targetTime);
        List<UserRoutineEntity> routines = userRoutineRepository.findAllByTargetArrivalTime(fiveMinutesAgo);
        List<UserEntity> users = userRepository.findAllByDepartureMinutes(targetTime + "분 전");

        for (UserRoutineEntity routine : routines) {

             UserEntity user = users.stream()
                    .filter(u -> u.getId().equals(routine.getUserId()))
                    .findFirst()
                    .orElse(null);
             if(user!=null)
                sendPush(
                        user.getAppPushToken(),
                        "출발 알림",
                        targetTime + "분 후 출발해야 합니다.",
                        "channel_vibrate"
                );
        }
    }

    private String getMode(String mode){
        if(mode.equals("소리"))
            return "channel_sound";
        else if (mode.equals("진동")) {
            return "channel_vibrate";
        }
        else
            return "channel_all";
    }

    private void sendPush(
            String token,
            String title,
            String body,
            String mode
    ) throws FirebaseMessagingException {
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(
                        AndroidNotification.builder()
                                .setChannelId(mode)
                                .build()
                )
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setAndroidConfig(androidConfig)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .build();

        FirebaseMessaging.getInstance().send(message);
    }


}
