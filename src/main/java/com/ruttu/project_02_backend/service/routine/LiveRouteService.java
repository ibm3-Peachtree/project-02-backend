package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.live.RoutineCompleteDto;
import com.ruttu.project_02_backend.dto.routine.odsay.*;
import com.ruttu.project_02_backend.dto.routine.live.*;
import com.ruttu.project_02_backend.dto.routine.location.CurrentLocationDto;
import com.ruttu.project_02_backend.dto.routine.routine.RouteListDto;
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
                saveTodayMyRouteAtRedis(userId, liveRouteForReportDto);
                saveTodayMyXYAtRedis(userId, routeXYForReportDto);
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
                saveTodayMyRouteAtRedis(userId, liveRouteForReportDto);
                saveTodayMyXYAtRedis(userId, routeXYForReportDto);
                return savedRoute; // 일치 경로 없으면 저장된 경로 반환
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void saveRecommendedRoute(Long userId, int recoId){
        RouteDto liveRouteForReportDto = routineService.readJson(
                (String) redisTemplate.opsForValue().get(routeFullKey(userId, recoId)),
                RouteDto.class
        );
        List<RouteXYDto> routeXYForReportDto = routineService.readJson(
                (String) redisTemplate.opsForValue().get(routeXyKey(userId, recoId)),
                new TypeReference<List<RouteXYDto>>() {}
        );

        saveTodayRecoRouteAtRedis(userId, new LiveRouteDto(liveRouteForReportDto));
        saveTodayRecoXYAtRedis(userId, routeXYForReportDto);
    }

    // 추천 경로 상세 조회
    @Transactional(readOnly = true)
    public RouteDto getRecommendedRouteDetail(Long userId, int recoId) {
        return routineService.readJson(
                (String) redisTemplate.opsForValue().get(routeFullKey(userId, recoId)),
                RouteDto.class
        );
    }

    // 추천 경로 목록 조회
    @Transactional(readOnly = true)
    public List<RouteListDto> getRecommendedRoute(
            Long userId
    ) {

        // 주소를 조회하여 lat, lng 값 가져오기
        UserRoutineEntity routine = getTodayRoutine(userId);

        OdsayXYDto xy = odsayIOService.getOdsayXyByAlias(
                userId, routine.getOriginAlias(), routine.getDestinationAlias());

        // odsay 경로 조회
        try {

            OdsayResponseDto routes = odsayIOService.getOdsay(xy);

            List<OdsayPathDto> paths = odsayIOService.getPath(routes, 3); // pathType: 1-지하철, 2-버스, 3-버스+지하철
            List<OdsayPathDto.Info> infos = odsayIOService.getInfo(paths);
            List<List<OdsayPathDto.SubPath>> subPaths = odsayIOService.getSubPaths(paths);


            // 상세보기 버전 생성하여 Redis에 넣기
            saveDetailRoutes(subPaths, infos, userId);

            // x,y 좌표 저장
            saveRouteXY(paths, userId);

            List<List<String>> trafficTypes = odsayIOService.getTrafficTypeNo(subPaths);
            return IntStream.range(0, infos.size())
                    .mapToObj(i -> {

                                RouteListDto resRouteList = new RouteListDto(
                                        i,
                                        trafficTypes.get(i),
                                        infos.get(i).getTotalTime(),
                                        infos.get(i).getPayment()
                                );

                                // redis에 저장
                                String key = routeSummaryKey(userId, i);

                                String json = mapper.writeValueAsString(resRouteList);
                                redisTemplate.opsForValue().set(key, json);
                                System.out.println("saved " + key);

                                return resRouteList;
                            }
                    )
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }

    }


    @Transactional(readOnly = true)
    public CurrentSectionDto getMyCurrentSection(Long userId){
        CurrentXYDto xy = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getLocationKey(userId)),
                CurrentXYDto.class
        );
        UserRoutineEntity routine = getTodayRoutine(userId);
        if (routine == null) return null; // null 체크 추가

        RouteXYForReportDto routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getTodayMyXYKey(userId)),
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

    @Transactional(readOnly = true)
    public CurrentSectionDto getRecoCurrentSection(Long userId){
//        return getCurrentSection(userId, , ;
        CurrentXYDto xy = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getLocationKey(userId)),
                CurrentXYDto.class
        );

        List<RouteXYDto> routeXY = routineService.readJson(
                (String) redisTemplate.opsForValue().get(getTodayRecoXYKey(userId)),
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
                routeXY.stream()
                        .map(RouteXYDto::getNo).toList(),
                routeXY
        );
    }


//    private CurrentSectionDto getCurrentSection(Long userId, String routeKey, String xyKey){
//        CurrentXYDto xy = routineService.readJson(
//                (String) redisTemplate.opsForValue().get(getLocationKey(userId)),
//                CurrentXYDto.class
//        );
//        UserRoutineEntity routine = getTodayRoutine(userId);
//        if (routine == null) return null; // null 체크 추가
//        Long routineId = routine.getId();
//        LiveRouteForReportDto route = routineService.readJson(
//                (String) redisTemplate.opsForValue().get(routeKey),
//                new TypeReference<LiveRouteForReportDto>() {}
//        );
//        RouteXYForReportDto routeXY = routineService.readJson(
//                (String) redisTemplate.opsForValue().get(xyKey),
//                new TypeReference<RouteXYForReportDto>() {}
//        );
//
//        // 가장 가까운 지점 = 현재 향하고 있는 목표 지점
//        List<RouteXYDto> routeXYList = routeXY.getRouteXYDtoList();
//        int nearestIndex = IntStream.range(0, routeXYList.size())
//                .filter(i -> routeXYList.get(i) != null
//                        && routeXYList.get(i).getY() != null
//                        && routeXYList.get(i).getX() != null)
//                .boxed()
//                .min(Comparator.comparingDouble(i ->
//                        distanceMeters(
//                                xy.getLatitude(), xy.getLongitude(),
//                                routeXYList.get(i).getY(), routeXYList.get(i).getX())
//                ))
//                .orElse(-1);
//
//        return new CurrentSectionDto(
//                nearestIndex,
//                routeXYList.stream()
//                        .map(RouteXYDto::getNo).toList(),
//                routeXYList
//        );
//    }

    @Transactional
    public void myRoutecompleted(Long userId, RoutineCompleteDto routineCompleteDto){
        // redis 데이터 불러오기
        String myRouteKey = getTodayMyRouteKey(userId);
        String myXyKey = getTodayMyXYKey(userId);

        saveDB(myRouteKey, myXyKey, userId, routineCompleteDto);
    }

    @Transactional
    public void recoRoutecompleted(Long userId, RoutineCompleteDto routineCompleteDto){
        // redis 데이터 불러오기
        String recoRouteKey = getTodayRecoRouteKey(userId);
        String recoXyKey = getTodayRecoXYKey(userId);

        saveDB(recoRouteKey, recoXyKey, userId, routineCompleteDto);
    }

    private void saveDB(String routeKey, String xyKey, Long userId, RoutineCompleteDto routineCompleteDto) {
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
    private String getTodayMyRouteKey(Long userId){
        return "routine:live:my:route:user:" + userId;
    }
    private String getTodayMyXYKey(Long userId){
        return "routine:live:my:xy:user:" + userId;
    }

    private void saveTodayMyRouteAtRedis(Long userId, LiveRouteForReportDto liveRouteForReportDto){
        String key = getTodayMyRouteKey(userId);
        String json = mapper.writeValueAsString(liveRouteForReportDto);
        redisTemplate.opsForValue().set(key, json);
    }
    private void saveTodayMyXYAtRedis(Long userId, RouteXYForReportDto routeXYForReportDto){
        String key = getTodayMyXYKey(userId);
        String json = mapper.writeValueAsString(routeXYForReportDto);
        redisTemplate.opsForValue().set(key, json);
    }
    private static String routeFullKey(Long userId, int recoId) {
        return "routine:live:reco:route:full:user:" + userId + ":" + recoId;
    }
    private static String routeSummaryKey(Long userId, int recoId) {
        return "routine:live:reco:route:summary:user:" + userId + ":" + recoId;
    }
    private static String routeXyKey(Long userId, int recoId) {
        return "routine:live:reco:route:xy:user:" + userId + ":" + recoId;
    }

    private void saveRouteXY(List<OdsayPathDto> path,
                             Long userId) {

        List<List<RouteXYDto>> stations = odsayIOService.getRouteXY(path);

        IntStream.range(0, stations.size())
                .forEach(i -> {
                    String key = routeXyKey(userId, i);

                    String json = mapper.writeValueAsString(stations.get(i));
                    redisTemplate.opsForValue().set(key, json);
                    System.out.println("saved " + key);
                });
    }


    private void saveDetailRoutes(
            List<List<OdsayPathDto.SubPath>> subPaths,
            List<OdsayPathDto.Info> infos,
            Long userId
    ) {
        List<List<RouteSectionDto>> detailRoutes = odsayIOService.getDetailPaths(subPaths);
        IntStream.range(0, infos.size())
                .forEach(i -> {

                    RouteDto routeDto = new RouteDto(
                            i,
                            infos.get(i).getTotalDistance(),
                            infos.get(i).getTotalTime(),
                            infos.get(i).getPayment(),
                            infos.get(i).getFirstStartStation(),
                            infos.get(i).getLastEndStation(),
                            detailRoutes.get(i)
                    );

                    String key = routeFullKey(userId, i);
                    String json = mapper.writeValueAsString(routeDto);
                    redisTemplate.opsForValue().set(key, json);
                    System.out.println("saved " + key);

                });
    }

    private String getTodayRecoXYKey(Long userId){
        return "routine:live:reco:xy:user:" + userId;
    }
    private String getTodayRecoRouteKey(Long userId){
        return "routine:live:reco:route:user:" + userId;
    }
    private void saveTodayRecoRouteAtRedis(Long userId, LiveRouteDto liveRouteForReportDto){
        String key = getTodayRecoRouteKey(userId);
        String json = mapper.writeValueAsString(liveRouteForReportDto);
        redisTemplate.opsForValue().set(key, json);
    }

    private void saveTodayRecoXYAtRedis(Long userId, List<RouteXYDto> routeXYForReportDto){
        String key = getTodayRecoXYKey(userId);
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
