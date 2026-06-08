package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.live.*;
import com.ruttu.project_02_backend.dto.routine.location.CurrentSectionDto;
import com.ruttu.project_02_backend.dto.routine.location.CurrentXYDto;
import com.ruttu.project_02_backend.dto.routine.location.CurrentLocationDto;
import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteXYDto;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.exception.routine.RouteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LiveLocationService {
    private final LiveRouteService liveRouteService;
    private final RoutineService routineService;

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    @Transactional
    public void updateLocation(Long userId, LiveLocationDto liveLocationDto) {

        String key = "location:user:" + userId;
        String json = mapper.writeValueAsString(liveLocationDto);
        redisTemplate.opsForList().rightPush(key, json);
        System.out.println("saved " + key);

    }

    @Transactional(readOnly = true)
    public void sendRouteProgress(String principalName, LiveLocationDto liveLocationDto) {
        CurrentLocationDto dto = new CurrentLocationDto();
        dto.setUpdatedAt(Instant.now().toEpochMilli());

        Double speed = liveLocationDto.getSpeed();

        if (speed.isNaN()) {
            dto.setStatus("대기중");

            messagingTemplate.convertAndSendToUser(
                    principalName,
                    "/queue/status",
                    dto
            );
        }
        dto.setStatus(getStatus(speed));
        messagingTemplate.convertAndSendToUser(
                principalName,
                "/queue/status",
                dto
        );
    }

    @Transactional(readOnly = true)
    public void getMyCurrentSection(Long userId, String principalName, LiveLocationDto liveLocationDto){
        CurrentXYDto xy = new CurrentXYDto(liveLocationDto);
        UserRoutineEntity routine = liveRouteService.getRoutine(userId);
        if (routine == null) throw new RouteNotFoundException("루틴 없음");

        RouteXYForReportDto routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(
                        liveRouteService.getTodayMyXYKey(userId)),
                new TypeReference<RouteXYForReportDto>() {}
        );
        System.out.println("나의 경로 레디스 통과");

        // 가장 가까운 지점 = 현재 향하고 있는 목표 지점
        List<RouteXYDto> routeXYList = routeXY.getRouteXYDtoList();
        int nearestIndex = IntStream.range(0, routeXYList.size())
                .filter(i -> routeXYList.get(i) != null
                        && routeXYList.get(i).getY() != null
                        && routeXYList.get(i).getX() != null)
                .boxed()
                .min(Comparator.comparingDouble(i ->
                        distanceMeters(
                                xy.getLatitude(), xy.getLongitude(),
                                routeXYList.get(i).getY(), routeXYList.get(i).getX())
                ))
                .orElse(-1);
        System.out.println("index: " + nearestIndex);
        if (nearestIndex >= 0)
            messagingTemplate.convertAndSendToUser(
                    principalName,
                "/queue/location/my",
                new CurrentSectionDto(
                        nearestIndex,
                        routeXYList.stream()
                                .map(RouteXYDto::getNo).toList(),
                        routeXYList
                )
        );
    }

    @Transactional(readOnly = true)
    public void getRecoCurrentSection(Long userId, String principalName, LiveLocationDto liveLocationDto){
        CurrentXYDto xy = new CurrentXYDto(liveLocationDto);

        List<RouteXYDto> routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(
                        liveRouteService.getTodayRecoXYKey(userId)),
                new TypeReference<List<RouteXYDto>>() {}
        );
        System.out.println("추천 경로 레디스 통과");

        // 가장 가까운 지점 = 현재 향하고 있는 목표 지점
        double threshold = Math.max(xy.getAccuracy(), 100.0);

        int nearestIndex = IntStream.range(0, routeXY.size())
                .filter(i -> routeXY.get(i) != null
                        && routeXY.get(i).getY() != null
                        && routeXY.get(i).getX() != null)
                .boxed()
                .min(Comparator.comparingDouble(i ->
                        distanceMeters(
                                xy.getLatitude(), xy.getLongitude(),
                                routeXY.get(i).getY(), routeXY.get(i).getX())
                ))
                .filter(i ->
                        distanceMeters(
                                xy.getLatitude(), xy.getLongitude(),
                                routeXY.get(i).getY(), routeXY.get(i).getX())
                                <= threshold
                )
                .orElse(-1);
        System.out.println("index: " + nearestIndex);

        if (nearestIndex >= 0)
            messagingTemplate.convertAndSendToUser(
                    principalName,
                "/queue/location/reco",
                new CurrentSectionDto(
                        nearestIndex,
                        routeXY.stream()
                                .map(RouteXYDto::getNo).toList(),
                        routeXY
                )
        );
    }


    private double distanceMeters(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


    private String getStatus(double speed){
        if(speed < 0.3){
            return "대기중";
        } else if (speed < 2.2) {
            return "도보중";
        } else {
            return "탑승중";
        }
    }
}
