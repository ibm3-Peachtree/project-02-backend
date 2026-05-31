package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.live.RoutineCompleteDto;
import com.ruttu.project_02_backend.dto.routine.odsay.*;
import com.ruttu.project_02_backend.dto.routine.live.*;
import com.ruttu.project_02_backend.dto.routine.location.CurrentLocationDto;
import com.ruttu.project_02_backend.entity.stats.UserDailyStatsEntity;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.exception.routine.RoutineNotFoundException;
import com.ruttu.project_02_backend.repository.stats.UserDailyStatsRepository;
import com.ruttu.project_02_backend.repository.prod.routine.UserRoutineRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class LiveRouteService {

    private final UserRoutineRepository userRoutineRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    private final OdsayIOService odsayIOService;
    private final RoutineService routineService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional(readOnly = true)
    public CurrentLocationDto getRouteProgress(Long userId) {
        SpeedDto speed = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getLocationKey(userId)),
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

    @Transactional
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

                LiveRouteForReportDto liveRouteForReportDto = new LiveRouteForReportDto(
                        routine.getId(),
                        liveRoute
                );
                RouteXYForReportDto routeXYForReportDto = new RouteXYForReportDto(
                        routine.getId(),
                        routeListXY.get(matchedIndex)
                );
                saveTodayRouteAtRedis(userId, liveRouteForReportDto);
                saveTodayXYAtRedis(userId, routeXYForReportDto);
                return liveRoute;
            } else {
                System.out.println("일치하는 경로 없음");

                LiveRouteForReportDto liveRouteForReportDto = new LiveRouteForReportDto(
                        routine.getId(),
                        savedRoute
                );
                RouteXYForReportDto routeXYForReportDto = new RouteXYForReportDto(
                        routine.getId(),
                        savedRouteXY
                );
                saveTodayRouteAtRedis(userId, liveRouteForReportDto);
                saveTodayXYAtRedis(userId, routeXYForReportDto);
                return savedRoute; // 일치 경로 없으면 저장된 경로 반환
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
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
            LiveRouteForReportDto liveRouteForReportDto = new LiveRouteForReportDto(
                    routine.getId(),
                    liveRoute
            );
            RouteXYForReportDto routeXYForReportDto = new RouteXYForReportDto(
                    routine.getId(),
                    routeListXY.getFirst()
            );
            saveTodayRouteAtRedis(userId, liveRouteForReportDto);
            saveTodayXYAtRedis(userId, routeXYForReportDto);
            return liveRoute;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public CurrentSectionDto getCurrentSection(Long userId){
        CurrentXYDto xy = routineService.readJson(
                (String) redisTemplate.opsForValue().get("location:user:" + userId),
                CurrentXYDto.class
        );
        UserRoutineEntity routine = getTodayRoutine(userId);
        if (routine == null) return null; // null 체크 추가
        Long routineId = routine.getId();
        LiveRouteForReportDto route = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getTodayRouteKey(userId)),
                new TypeReference<LiveRouteForReportDto>() {}
        );
        RouteXYForReportDto routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getTodayXYKey(userId)),
                new TypeReference<RouteXYForReportDto>() {}
        );

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

        return new CurrentSectionDto(
                nearestIndex,
                routeXYList.stream()
                        .map(RouteXYDto::getNo).toList(),
                routeXYList
        );
    }

    @Transactional
    public void completed(Long userId, RoutineCompleteDto routineCompleteDto){
        // redis 데이터 불러오기
        String routeKey = getTodayRouteKey(userId);
        String xyKey = getTodayXYKey(userId);
        LiveRouteForReportDto todayRoute = routineService.readJson(
                (String) redisTemplate.opsForValue().get(routeKey),
                new TypeReference<LiveRouteForReportDto>() {}
        );
        RouteXYForReportDto todayXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(xyKey),
                new TypeReference<RouteXYForReportDto>() {}
        );
        // 칼로리와 쾌적함을 구해야 함..


        // db에 저장
        Long routineId = todayRoute.getRoutineId();

        UserRoutineEntity userRoutineEntity = userRoutineRepository.findById(routineId)
                .orElseThrow(RoutineNotFoundException::new);

        TodayRoutineForDBDto todayRoutineForDBDto = new TodayRoutineForDBDto(
                userId,
                routineId,
                routineCompleteDto.getDepartureTime(),
                routineCompleteDto.getArrivalTime(),
                todayXY.getRouteXYDtoList(),
                todayRoute.getPayment(),
                todayRoute.getTotalDistance(),
                0, // 하드 코딩
                isNegativeDifference(
                        userRoutineEntity.getTargetArrivalTime(),
                        routineCompleteDto.getArrivalTime()),
                userRoutineEntity.getPreferredRouteXy().equals(todayXY),
                true, // 하드 코딩
                routineCompleteDto.getSatWaitTimeScore(),
                routineCompleteDto.getSatEtaScore(),
                routineCompleteDto.getSatRouteScore(),
                LocalDate.now(KST)
        );
        UserDailyStatsEntity entity = new UserDailyStatsEntity();
        entity.setUserId(userId);
        entity.setUserRoutineId(routineId);  // routineId → userRoutineId 명시
        entity.setDepartureTime(routineCompleteDto.getDepartureTime());
        entity.setArrivalTime(routineCompleteDto.getArrivalTime());
        entity.setTodayRoutine(todayXY.getRouteXYDtoList());
        entity.setTransportCost(todayRoute.getPayment());
        entity.setTotalDistanceMeter(todayRoute.getTotalDistance());
        entity.setEstimatedCalories(0);
        entity.setLate(isNegativeDifference(
                userRoutineEntity.getTargetArrivalTime(),
                routineCompleteDto.getArrivalTime()));
        entity.setRouteFollowed(
                userRoutineEntity.getPreferredRouteXy()
                        .equals(todayXY.getRouteXYDtoList())); // 수정
        entity.setComfort(true);
        entity.setSatWaitTimeScore(routineCompleteDto.getSatWaitTimeScore());
        entity.setSatEtaScore(routineCompleteDto.getSatEtaScore());
        entity.setSatRouteScore(routineCompleteDto.getSatRouteScore());
        entity.setDate(LocalDate.now(KST));
        userDailyStatsRepository.save(entity);

        // redis 삭제
        redisTemplate.delete(routeKey);
        redisTemplate.delete(xyKey);
    }

    private boolean isNegativeDifference(LocalTime targetArrivalTime,
                                        LocalTime arrivalTime) {
        long seconds = Duration.between(arrivalTime, targetArrivalTime).getSeconds();
        return seconds < 0;
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
    private String getLocationKey(Long userId){
        return "location:user:" + userId;
    }
    private String getTodayRouteKey(Long userId){
        return "routine:live:route:user:" + userId;
    }
    private String getTodayXYKey(Long userId){
        return "routine:live:xy:user:" + userId;
    }

    private void saveTodayRouteAtRedis(Long userId, LiveRouteForReportDto liveRouteForReportDto){
        String key = getTodayRouteKey(userId);
        String json = mapper.writeValueAsString(liveRouteForReportDto);
        redisTemplate.opsForValue().set(key, json);
    }

    private void saveTodayXYAtRedis(Long userId, RouteXYForReportDto routeXYForReportDto){
        String key = getTodayXYKey(userId);
        String json = mapper.writeValueAsString(routeXYForReportDto);
        redisTemplate.opsForValue().set(key, json);
    }
    private UserRoutineEntity getTodayRoutine(Long userId){
        return userRoutineRepository.findAllByUserId(userId)
                .stream()
                .filter(r -> isToday(r.getPreferredDowMask()))
                .min(Comparator.comparingInt(item ->
                        Math.abs(LocalTime.now(KST).toSecondOfDay()
                                - item.getRecoDepartureTime().toSecondOfDay())
                ))
                .orElse(null);
    }

    private boolean isToday(long mask) {
        int todayIndex = LocalDate.now(KST).getDayOfWeek().getValue() - 1;
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
