package com.ruttu.project_02_backend.service.routine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruttu.project_02_backend.dto.routine.Odsay.*;
import com.ruttu.project_02_backend.dto.routine.live.CurrentSectionDto;
import com.ruttu.project_02_backend.dto.routine.live.CurrentXYDto;
import com.ruttu.project_02_backend.dto.routine.live.LiveRouteDto;
import com.ruttu.project_02_backend.dto.routine.live.SpeedDto;
import com.ruttu.project_02_backend.dto.routine.location.CurrentLocationDto;
import com.ruttu.project_02_backend.entity.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.repository.routine.UserRoutineRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class LiveRouteService {

    private final UserRoutineRepository userRoutineRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    private final OdsayIOService odsayIOService;
    private final RoutineService routineService;

    public CurrentLocationDto getRouteProgress(Long userId) {
        SpeedDto speed = routineService.readJson(
                (String) redisTemplate.opsForValue().get("location:user:" + userId),
                SpeedDto.class
        );
        CurrentLocationDto dto = new CurrentLocationDto();
        dto.setUpdatedAt(Instant.now().toEpochMilli());

        if (speed == null) {
            dto.setStatus("대기중");
            return dto;
        }
        dto.setStatus(getStatus(speed));
        return dto;
    }

    public LiveRouteDto getMyRoute(Long userId) {
        UserRoutineEntity routine = getTodayRoutine(userId);

        // routine 없으면 null 반환
        if (routine == null) return null;

        LiveRouteDto savedRoute = new LiveRouteDto(routine.getPreferredRoute());
        List<RouteXYDto> savedRouteXY = routine.getPreferredRouteXy();

        OdsayXYDto xy = odsayIOService.getOdsayXyByAlias(
                userId, routine.getOriginAlias(), routine.getDestinationAlias());

        try {
            OdsayResponseDto routes = odsayIOService.getOdsay(xy);
            List<OdsayPathDto> paths = odsayIOService.getPath(routes, 5);
            List<OdsayPathDto.Info> infos = odsayIOService.getInfo(paths);
            List<List<OdsayPathDto.SubPath>> subPaths = odsayIOService.getSubPaths(paths);
            List<List<RouteXYDto>> routeListXY = odsayIOService.getRouteXY(paths);

            int matchedIndex = IntStream.range(0, routeListXY.size())
                    .filter(i -> routeListXY.get(i).equals(savedRouteXY))
                    .findFirst()
                    .orElse(-1);

            if (matchedIndex >= 0) {
                List<OdsayPathDto.SubPath> matchedPath = subPaths.get(matchedIndex);
                LiveRouteDto liveRoute = new LiveRouteDto(odsayIOService.getDetailRoutes(
                        List.of(matchedPath),
                        List.of(infos.get(matchedIndex))
                ).getFirst());

                saveTodayRouteAtRedis(routine.getId(), userId, liveRoute);
                saveTodayXYAtRedis(routine.getId(), userId, routeListXY.get(matchedIndex));
                return liveRoute;
            } else {
                System.out.println("일치하는 경로 없음");
                saveTodayRouteAtRedis(routine.getId(), userId, savedRoute);
                saveTodayXYAtRedis(routine.getId(), userId, savedRouteXY);
                return savedRoute; // 일치 경로 없으면 저장된 경로 반환
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LiveRouteDto getRecommendedRoute(Long userId) {
        UserRoutineEntity routine = getTodayRoutine(userId);

        // routine 없으면 null 반환
        if (routine == null) return null;

        OdsayXYDto xy = odsayIOService.getOdsayXyByAlias(
                userId, routine.getOriginAlias(), routine.getDestinationAlias());

        try {
            OdsayResponseDto routes = odsayIOService.getOdsay(xy);
            List<OdsayPathDto> paths = odsayIOService.getPath(routes, 1);
            List<OdsayPathDto.Info> infos = odsayIOService.getInfo(paths);
            List<List<OdsayPathDto.SubPath>> subPaths = odsayIOService.getSubPaths(paths);
            List<List<RouteXYDto>> routeListXY = odsayIOService.getRouteXY(paths);

            LiveRouteDto liveRoute = new LiveRouteDto(odsayIOService.getDetailRoutes(
                    subPaths,
                    infos
            ).getFirst());

            // 보고서용에 활용할 데이터 저장
            saveTodayRouteAtRedis(routine.getId(), userId, liveRoute);
            saveTodayXYAtRedis(routine.getId(), userId, routeListXY.getFirst());
            return liveRoute;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CurrentSectionDto getCurrentSection(Long userId){
        CurrentXYDto xy = routineService.readJson(
                (String) redisTemplate.opsForValue().get("location:user:" + userId),
                CurrentXYDto.class
        );
        UserRoutineEntity routine = getTodayRoutine(userId);
        if (routine == null) return null; // null 체크 추가
        Long routineId = routine.getId();

        List<RouteXYDto> routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get("routine:my:xy:" + routineId + ":user:" + userId),
                new TypeReference<List<RouteXYDto>>() {}
        );
        // 가장 가까운 지점 = 현재 향하고 있는 목표 지점
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
                .orElse(-1);

        return new CurrentSectionDto(
                nearestIndex,
                routeXY.stream().map(r-> r.getType()).toList()
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


    private void saveTodayRouteAtRedis(Long routineId, Long userId, LiveRouteDto liveRouteDto){
        String key = "routine:my:route:" + routineId + ":user:" + userId;
        String json = mapper.writeValueAsString(liveRouteDto);
        redisTemplate.opsForValue().set(key, json);
    }

    private void saveTodayXYAtRedis(Long routineId, Long userId, List<RouteXYDto> routeXYDto){
        String key = "routine:my:xy:" + routineId + ":user:" + userId;
        String json = mapper.writeValueAsString(routeXYDto);
        redisTemplate.opsForValue().set(key, json);
    }

    private UserRoutineEntity getTodayRoutine(Long userId){
        return userRoutineRepository.findAllByUserId(userId)
                .stream()
                .filter(r -> isToday(r.getPreferredDowMask()))
                .min(Comparator.comparingInt(item ->
                        Math.abs(LocalTime.now().toSecondOfDay()
                                - item.getRecoDepartureTime().toSecondOfDay())
                ))
                .orElse(null);
    }


    private boolean isToday(long mask) {
        int todayIndex = LocalDate.now().getDayOfWeek().getValue() - 1;
        return (mask & (1L << todayIndex)) != 0;
    }

    private String getStatus(SpeedDto speedDto){
        double speed = speedDto.getSpeed();

        if(speed < 0.3){
            return "대기중";
        } else if (speed < 2.2) {
            return "도보중";
        } else {
            return "탑승중";
        }
    }
}
