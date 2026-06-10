package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.live.RoutineCompleteDto;
import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import com.ruttu.project_02_backend.dto.routine.odsay.*;
import com.ruttu.project_02_backend.dto.routine.live.*;
import com.ruttu.project_02_backend.dto.routine.routine.RouteListDto;
import com.ruttu.project_02_backend.entity.stats.UserDailyStatsEntity;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.exception.routine.RoutineNotFoundException;
import com.ruttu.project_02_backend.repository.stats.UserDailyStatsRepository;
import com.ruttu.project_02_backend.repository.prod.routine.UserRoutineRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.*;
import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class LiveRouteService {
    private static final Logger log =
            LoggerFactory.getLogger(LiveRouteService.class);

    private final UserRoutineRepository userRoutineRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    private final OdsayIOService odsayIOService;
    private final RoutineService routineService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public LiveRouteDto getMyRoute(Long userId, Long routineId) {
        UserRoutineEntity routine;
        if (routineId == null){
            routine = getTodayRoutine(userId);
        }else {
            routine = getRoutine(routineId);
        }

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
                System.out.println("myroute: 오디세이");

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
                System.out.println("myroute: 오디세이에 없음");
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

    @Transactional
    public void saveDetourRoute(Long userId, int pathId){
        List<DetourDto> detourList = getDetourList(userId);
        DetourDto detour = detourList.get(pathId);

        List<RouteSectionDto> routeSectionDtoList = new ArrayList<>();
        detour.getPath_segments()
                .stream()
                .forEach(
                        p -> {
                            String trafficType = p.getDisplay_name().getFirst();
                            String engType = getEngType(trafficType);
                            routeSectionDtoList.add(
                                    switch (engType) {
                                        case "walk" -> new DetourWalkSectionDto(p);
                                        case "bus" -> new DetourBusSectionDto(p);
                                        case "subway" -> new DetourSubwaySectionDto(p);
                                        default -> throw new IllegalArgumentException("지원하지 않는 타입: " + engType);
                                    }
                            );
                        });


        RouteDto liveRouteForReportDto = new RouteDto(detour.getPath_id(),
                detour.getPath_segments().stream().mapToInt(DetourDto.pathSegments::getTotal_distance_m).sum()/1000, //km
                (int) detour.getTotal_duration_min(),
                detour.getCost(),
                detour.getPath_segments().stream()
                        .filter(p -> !p.getDisplay_name().getFirst().equals("도보"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("도보가 아닌 구간이 없습니다."))
                        .getStations()
                        .getFirst()
                        .getName(),
                detour.getPath_segments().getLast().getStations().getLast().getName(),
                routeSectionDtoList);
        List<RouteXYDto> routeXYForReportDto =
                detour.getPath_segments()
                        .stream()
                        .filter(p -> !p.getDisplay_name().getFirst().equals("도보"))
                        .flatMap(p -> {

                            String trafficType =
                                    p.getDisplay_name().getFirst();

                            String engType =
                                    getEngType(trafficType);

                            return p.getStations()
                                    .stream()
                                    .map(s -> new RouteXYDto(
                                            s.getName(),
                                            s.getX(),
                                            s.getY(),
                                            s.getArs_id(),
                                            engType,
                                            engType + ":" + trafficType
                                    ));
                        })
                        .toList();


        saveTodayRecoRouteAtRedis(userId, new LiveRouteDto(liveRouteForReportDto));
        saveTodayRecoXYAtRedis(userId, routeXYForReportDto);
    }
    public DetourDto getDetour(Long userId, int pathId){
        List<DetourDto> detourList = getDetourList(userId);
        return detourList.get(pathId);
    }
    @Transactional(readOnly = true)
    public void sendIncidentsDetour(Long userId, String principalName){
        try {
            Set<Object> jsonSet = Optional.ofNullable(redisTemplate.opsForSet()
                    .members(getIncidentsKey(userId)))
                    .orElse(Collections.emptySet());

            List<String> incidents = jsonSet.stream()
                    .map(json -> routineService.readJson(
                            json.toString(),
                            IncidentsDto.class
                    ))
                    .map(IncidentsDto::getIncident)
                    .toList();

            List<DetourDto> detourList = getDetourList(userId);

            // null이면 전송 스킵
            if (!incidents.isEmpty()) {
                System.out.println("incident 전송 중!!!");
                messagingTemplate.convertAndSendToUser(
                        principalName,
                        "/queue/incident",
                        incidents
                );
            }

            if (detourList != null && !detourList.isEmpty()) {
                messagingTemplate.convertAndSendToUser(
                        principalName,
                        "/queue/detour",
                        detourList
                );
            }

        } catch (Exception e) {  // IllegalStateException → Exception으로 확장
            log.debug("incident/detour 전송 스킵. userId={}", userId);
        }
    }

    @Transactional(readOnly = true)
    public List<DetourDto> getDetourList(Long userId){
        return routineService.readJson(
                (String) redisTemplate.opsForValue().get(getDetourKey(userId)),
                new TypeReference<List<DetourDto>>() {}
        );
    }
    private String getIncidentsKey(Long userId) {
        return "user:incidents:" + userId;
    }
    private String getDetourKey(Long userId) {
        return "routine:live:incident:full:" + userId;
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
            Long userId,
            Long routineId
    ) {

        // 주소를 조회하여 lat, lng 값 가져오기
        UserRoutineEntity routine;
        if (routineId == null){
            routine = getTodayRoutine(userId);
        }else {
            routine = getRoutine(routineId);
        }

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

    @Transactional
    public void myRoutecompleted(Long userId, RoutineCompleteDto routineCompleteDto){
        // redis 데이터 불러오기
        String myRouteKey = getTodayMyRouteKey(userId);
        String myXyKey = getTodayMyXYKey(userId);

        // location 삭제
        redisTemplate.delete(getLocationKey(userId));
        saveDB(myRouteKey, myXyKey, userId, routineCompleteDto);
    }

    @Transactional
    public void recoRoutecompleted(Long userId, RoutineCompleteDto routineCompleteDto){
        // redis 데이터 불러오기
        String recoRouteKey = getTodayRecoRouteKey(userId);
        String recoXyKey = getTodayRecoXYKey(userId);

        // location 삭제
        redisTemplate.delete(getLocationKey(userId));
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

        // calories
        int totalWalkTimeMin = todayRoute.getPath()
                .stream()
                .filter(r -> "walk".equals(r.getType()))
                .mapToInt(RouteSectionDto::getSectionTime)
                .sum();

        // comfort
        List<Object> locationList = redisTemplate.opsForList()
                 .range(getLocationKey(userId), 0, -1);

        List<LiveLocationDto> walkSpeed = locationList.stream()
                .map(v ->
                    routineService.readJson(
                        (String) redisTemplate.opsForList().index(getLocationKey(userId), -1),
                        LiveLocationDto.class
                ))
                .filter(l -> "walk".equals(l.getType()))
                .toList();
        double comfortIndex =  (double) (walkSpeed
                .stream()
                .filter(l -> l.getSpeed() >= 2 && l.getSpeed() <= 4.5)
                .count())/walkSpeed.size();

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
                getCalories(totalWalkTimeMin),
                !routineCompleteDto.getDepartureTime()
                        .isBefore(userRoutineEntity.getRecoDepartureTime())
                        && isNegativeDifference(
                        userRoutineEntity.getTargetArrivalTime(),
                        routineCompleteDto.getArrivalTime()),
                todayXY.equals(userRoutineEntity.getPreferredRouteXy()),
                comfortIndex>0.3,
                routineCompleteDto.getSatWaitTimeScore(),
                routineCompleteDto.getSatEtaScore(),
                routineCompleteDto.getSatRouteScore(),
                LocalDate.now(KST)
        );
        UserDailyStatsEntity entity = new UserDailyStatsEntity();
        entity.setUserId(userId);
        entity.setUserRoutineId(routineId);
        entity.setDepartureTime(routineCompleteDto.getDepartureTime());
        entity.setArrivalTime(routineCompleteDto.getArrivalTime());
        entity.setTodayRoutine(todayXY.getRouteXYDtoList());
        entity.setTransportCost(todayRoute.getPayment());
        entity.setTotalDistanceMeter(todayRoute.getTotalDistance());
        entity.setEstimatedCalories(todayRoutineForDBDto.getEstimatedCalories());
        entity.setLate(isNegativeDifference(
                userRoutineEntity.getTargetArrivalTime(),
                routineCompleteDto.getArrivalTime()));
        entity.setRouteFollowed(
                userRoutineEntity.getPreferredRouteXy()
                        .equals(todayXY.getRouteXYDtoList()));
        entity.setComfort(todayRoutineForDBDto.isComfort());
        entity.setSatWaitTimeScore(routineCompleteDto.getSatWaitTimeScore());
        entity.setSatEtaScore(routineCompleteDto.getSatEtaScore());
        entity.setSatRouteScore(routineCompleteDto.getSatRouteScore());
        entity.setDate(LocalDate.now(KST));
        userDailyStatsRepository.save(entity);

        // redis 삭제
        redisTemplate.delete(routeKey);
        redisTemplate.delete(xyKey);
    }

    private String getEngType(String trafficType){
        if (trafficType.equals("도보"))
            return "walk";
        else if (trafficType.contains("호선")) {
            return "subway";
        }
        else{
            return "bus";
        }
    }
    private int getCalories(int walkMinutes){
        return (int) (3.5 * 65 * (walkMinutes / 60.0));
    }
    private boolean isNegativeDifference(LocalTime targetArrivalTime,
                                        LocalTime arrivalTime) {
        long seconds = Duration.between(arrivalTime, targetArrivalTime).getSeconds();
        return seconds < 0;
    }





    public String getLocationKey(Long userId){
        return "location:user:" + userId;
    }
    public String getTodayMyRouteKey(Long userId){
        return "routine:live:my:route:user:" + userId;
    }
    public String getTodayMyXYKey(Long userId){
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

    public String getTodayRecoXYKey(Long userId){
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

    public UserRoutineEntity getRoutine(Long routineId){
        return userRoutineRepository.findById(routineId)
                .orElse(null);
    }

    public UserRoutineEntity getTodayRoutine(Long userId){
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


}
