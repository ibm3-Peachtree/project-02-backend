package com.ruttu.project_02_backend.service.routine;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ruttu.project_02_backend.dto.routine.live.*;
import com.ruttu.project_02_backend.dto.routine.location.CurrentSectionDto;
import com.ruttu.project_02_backend.dto.routine.location.CurrentXYDto;
import com.ruttu.project_02_backend.dto.routine.location.CurrentLocationDto;
import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteXYDto;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.entity.prod.user.UserAddressEntity;
import com.ruttu.project_02_backend.exception.routine.RouteNotFoundException;
import com.ruttu.project_02_backend.repository.prod.user.UserAddressRepository;
import com.ruttu.project_02_backend.service.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LiveLocationService {
    private final UserAddressRepository userAddressRepository;
    private final LiveRouteService liveRouteService;
    private final RoutineService routineService;
    private final PushService pushService;

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
    public void getMyCurrentSection(
            Long userId, String principalName, LiveLocationDto liveLocationDto
    ){
        CurrentXYDto xy = new CurrentXYDto(liveLocationDto);
        UserRoutineEntity routine = liveRouteService.getTodayRoutine(userId);
        if (routine == null) throw new RouteNotFoundException("루틴 없음");


        RouteXYForReportDto routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(
                        liveRouteService.getTodayMyXYKey(userId)),
                new TypeReference<RouteXYForReportDto>() {}
        );

        // 가장 가까운 지점 = 현재 향하고 있는 목표 지점
        List<RouteXYDto> routeXYList = routeXY.getRouteXYDtoList();
        System.out.println("추천 경로 레디스 통과 => routeXYList size: " + routeXYList.size());

        int nearestIndex = getNearestIndex(routeXYList, xy);
        if (nearestIndex >= 0){
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
            RunGetOffNotification(userId, nearestIndex, routeXYList);
        }


    }

    @Transactional(readOnly = true)
    public void getRecoCurrentSection(Long userId, String principalName, LiveLocationDto liveLocationDto){
        CurrentXYDto xy = new CurrentXYDto(liveLocationDto);
        UserRoutineEntity routine = liveRouteService.getTodayRoutine(userId);
        if (routine == null) throw new RouteNotFoundException("루틴 없음");


        List<RouteXYDto> routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(
                        liveRouteService.getTodayRecoXYKey(userId)),
                new TypeReference<List<RouteXYDto>>() {}
        );
        System.out.println("추천 경로 레디스 통과 => routeXY size: " + routeXY.size());

        // 가장 가까운 지점 = 현재 향하고 있는 목표 지점
        int nearestIndex = getNearestIndex(routeXY, xy);
        if (nearestIndex >= 0){

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
            RunGetOffNotification(userId, nearestIndex, routeXY);
        }

    }

    private int getNearestIndex(List<RouteXYDto> routeXYList, CurrentXYDto xy) {
        double threshold = Math.max(xy.getAccuracy(), 100.0);

        int nearestIndex = IntStream.range(0, routeXYList.size()-1)
                .boxed()
                .filter(i -> {
                    RouteXYDto p = routeXYList.get(i);
                    if (p == null) return false;

                    // x/y 없는 도보 전환점
                    if (p.getX() == null || p.getY() == null) {
                        if (i < 1) return false;
                        RouteXYDto prev = routeXYList.get(i - 1);
                        if (prev.getX() == null || prev.getY() == null) return false;
                        if (i + 1 >= routeXYList.size()) return false;
                        RouteXYDto next = routeXYList.get(i + 1);
                        if (next.getX() == null || next.getY() == null) return false;

                        double totalDist = distanceMeters(prev.getY(), prev.getX(), next.getY(), next.getX());
                        double ratio = totalDist > 0 ? Math.min(50.0 / totalDist, 0.3) : 0.1;
                        double midX = prev.getX() + (next.getX() - prev.getX()) * ratio;
                        double midY = prev.getY() + (next.getY() - prev.getY()) * ratio;

                        double distToMid  = distanceMeters(xy.getLatitude(), xy.getLongitude(), midY, midX);
                        double distToNext = distanceMeters(xy.getLatitude(), xy.getLongitude(), next.getY(), next.getX());
                        return distToNext > distToMid;
                    }

                    // ✅ 일반 포인트: threshold 이내만 후보로 (min() 전에 걸러야 함)
                    return distanceMeters(
                            xy.getLatitude(), xy.getLongitude(),
                            p.getY(), p.getX()
                    ) <= threshold;
                })
                .min(Comparator.comparingDouble(i -> {
                    RouteXYDto p = routeXYList.get(i);
                    if (p.getX() == null || p.getY() == null) p = routeXYList.get(i - 1);
                    return distanceMeters(xy.getLatitude(), xy.getLongitude(), p.getY(), p.getX());
                }))
                .orElse(-1);

        RouteXYDto last = routeXYList.getLast();
        double distToLast = distanceMeters(
                xy.getLatitude(), xy.getLongitude(),
                last.getY(), last.getX()
        );
        System.out.println("distToLast: " + distToLast);
        System.out.println("last.getX(): " + last.getX());
        System.out.println("last.getY(): " + last.getY());
        if (distToLast <= 30) {
            nearestIndex = routeXYList.size() - 1;
            System.out.println("index: " + nearestIndex);
        }

        return nearestIndex;
    }

    private void RunGetOffNotification(Long userId, int nearestIndex, List<RouteXYDto> routeXYList){
        Map<String, Integer> lastIndexByNo = new HashMap<>();
        for (int i = 0; i < routeXYList.size(); i++) {
            RouteXYDto dto = routeXYList.get(i);
            if (!dto.getNo().equals("walk"))
                lastIndexByNo.put(dto.getNo(), i);
        }
        String trafficType = routeXYList.get(nearestIndex).getNo();
        if (!trafficType.equals("walk")){
            int remainingStations = lastIndexByNo.get(trafficType) - nearestIndex;
            System.out.println("remainingStations: "+remainingStations);
            if (remainingStations>0) {
                    String key = "last-getoff-index:" + trafficType + ":" + userId;
                try {

                    String lastIndex = (String) redisTemplate.opsForValue().get(key);
                    if (lastIndex==null)
                        redisTemplate.opsForValue().set(
                                key,
                                String.valueOf(nearestIndex)
                        );
                    else {
                        if (!Objects.equals(
                                Integer.parseInt(lastIndex),
                                nearestIndex)) {
                            pushService.sendGetOffNotification(userId, remainingStations);

                            redisTemplate.opsForValue().set(
                                    key,
                                    String.valueOf(nearestIndex)
                            );
                        }
                    }

                } catch (FirebaseMessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
