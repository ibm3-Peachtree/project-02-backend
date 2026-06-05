package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.odsay.*;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteSectionDto;
import com.ruttu.project_02_backend.dto.routine.routine.RouteListDto;
import com.ruttu.project_02_backend.dto.routine.routine.RoutineDetailDto;
import com.ruttu.project_02_backend.dto.routine.routine.RoutineDto;
import com.ruttu.project_02_backend.dto.routine.routine.RoutineListDto;
import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.exception.routine.DuplicateRoutineNameException;
import com.ruttu.project_02_backend.exception.routine.DuplicateRoutineTargetArrivalTimeException;
import com.ruttu.project_02_backend.exception.routine.RoutineNotFoundException;
import com.ruttu.project_02_backend.repository.prod.routine.UserRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final UserRoutineRepository userRoutineRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final OdsayIOService odsayIOService;
    private final ObjectMapper mapper;

    // 내 루틴 생성
    @Transactional
    public void createRoutine(RoutineDto routineDto, Long userId) {
        List<UserRoutineEntity> user = userRoutineRepository.findAllByUserId(userId);
        boolean duplicateName = user.stream()
                .anyMatch(r -> r.getRoutineName().equals(routineDto.getRoutineName()));

        if (duplicateName) {
            throw new DuplicateRoutineNameException();
        }

        boolean overlap = user.stream()
                .anyMatch(s ->
                        Objects.equals(
                                s.getTargetArrivalTime(),
                                routineDto.getTargetArrivalTime()
                        )
                                &&
                                hasDayOverlap(
                                        fromBitMask(s.getPreferredDowMask(), 7),
                                        routineDto.getDow()
                                )
                );

        // 동시간대 루틴 존재여부 확인
        if (overlap)
            throw new DuplicateRoutineTargetArrivalTimeException("동일 시간대 루틴 존재");

        // 내 루틴 저장
        UserRoutineEntity routine = new UserRoutineEntity();
        routine.setUserId(userId);
        saveUserRoutineEntity(routine, routineDto, userId);
    }

    // 내 루틴 삭제
    @Transactional
    public void deleteRoutine(Long routineId) {
        if (!userRoutineRepository.existsById(routineId))
            throw new RoutineNotFoundException("루틴 없음");

        userRoutineRepository.deleteById(routineId);
    }

    // 내 루틴 수정
    @Transactional
    public void updateRoutine(Long routineId, RoutineDto routineDto, Long userId) {
        UserRoutineEntity routine = userRoutineRepository.findById(routineId)
                .orElseThrow(() -> new RoutineNotFoundException("루틴 없음"));

    }

    // 내 루틴 조회
    @Transactional(readOnly = true)
    public List<RoutineListDto> getRoutine(Long userId) {

        List<UserRoutineEntity> routines = userRoutineRepository.findAllByUserId(userId);

//        if (routines.isEmpty()) {
//            throw new RoutineNotFoundException();
//        }

        return routines.stream()
                .map(r ->
                        new RoutineListDto(
                                r.getId(),
                                r.getRoutineName(),
                                r.getOriginAlias(),
                                r.getDestinationAlias(),
                                fromBitMask(r.getPreferredDowMask(), 7),
                                r.getTargetArrivalTime(),
                                r.getRecoDepartureTime(),
                                r.getSpareTime(),
                                r.isExcludeHoliday(),
                                r.getEstimatedDurationMin()
                        )
                )
                .toList();
    }

    // 루틴 상세 조회
    @Transactional(readOnly = true)
    public RoutineDetailDto getRoutineDetail(
            Long routineId) {
        return userRoutineRepository.findById(routineId)
                .map(
                        (r) -> new RoutineDetailDto(
                                r.getId(),
                                r.getRoutineName(),
                                r.getOriginAlias(),
                                r.getDestinationAlias(),
                                fromBitMask(r.getPreferredDowMask(), 7),
                                r.getTargetArrivalTime(),
                                r.getRecoDepartureTime(),
                                r.getEstimatedDurationMin(),
                                r.getSpareTime(),
                                r.isExcludeHoliday(),
                                r.getPreferredRoute()
                        )
                )
                .orElseThrow(RoutineNotFoundException::new);

    }
    // 경로 상세 조회
    @Transactional(readOnly = true)
    public RouteDto getRouteDetail(int recoId, Long userId) {
        return readJson(
                (String) redisTemplate.opsForValue().get(routeFullKey(userId, recoId)),
                RouteDto.class
        );
    }

    // 경로 목록 조회
    @Transactional(readOnly = true)
    public List<RouteListDto> getRoute(
            Long originId,
            Long destinationId,
            Long userId
    ) {       // 주소를 조회하여 lat, lng 값 가져오기
        OdsayXYDto xy = odsayIOService.getOdsayXyById(originId, destinationId);

        // odsay 경로 조회
        try {

            OdsayResponseDto routes = odsayIOService.getOdsay(xy);

            List<OdsayPathDto> paths = odsayIOService.getPath(routes, 5); // pathType: 1-지하철, 2-버스, 3-버스+지하철
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

    private void saveUserRoutineEntity(UserRoutineEntity r, RoutineDto dto, Long userId){
        long dowMask = toBitMask(dto.getDow());

        int recoId = dto.getRecoId(); // 추천 경로 ID
        RouteDto full = getFullRoute(recoId, userId); // 추천 경로
        List<RouteXYDto> xy = getXYList(recoId, userId); // 추천 경로 xy 목록

        int estimatedTime = full.getTotalTime(); // 예상 시간
        LocalTime departureTime = getDepartureTime(
                dto.getSpareTime(),// 추천 출발 시간
                dto.getTargetArrivalTime(),
                estimatedTime
        );

        r.setRoutineName(dto.getRoutineName());
        r.setOriginAlias(dto.getOriginAlias());
        r.setOrigin(dto.getOrigin());
        r.setDestinationAlias(dto.getDestinationAlias());
        r.setDestination(dto.getDestination());
        r.setTargetArrivalTime(dto.getTargetArrivalTime());
        r.setPreferredDowMask(dowMask);
        r.setPreferredRoute(full);
        r.setPreferredRouteXy(xy);
        r.setExcludeHoliday(dto.isExcludeHoliday());
        r.setSpareTime(dto.getSpareTime());
        r.setRecoDepartureTime(departureTime);
        r.setEstimatedDurationMin(estimatedTime);
        userRoutineRepository.save(r);

    }

    private List<RouteXYDto> getXYList(int recoId, Long userId){
        return readJson(
            (String) redisTemplate.opsForValue().get(routeXyKey(userId, recoId)),
            new TypeReference<List<RouteXYDto>>() {}
    );
    }
    private static String routeXyKey(Long userId, int recoId) {
        return "routine:route:xy:user:" + userId + ":" + recoId;
    }
    private RouteDto getFullRoute(int recoId, Long userId){
        return readJson(
            (String) redisTemplate.opsForValue().get(routeFullKey(userId, recoId)),
            RouteDto.class
    );
    }
    private static String routeFullKey(Long userId, int recoId) {
        return "routine:route:full:user:" + userId + ":" + recoId;
    }

    private static String routeSummaryKey(Long userId, int recoId) {
        return "routine:route:summary:user:" + userId + ":" + recoId;
    }
    public  <T> T readJson(String raw, Class<T> type) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Redis에 경로 데이터가 없습니다.");
        }
        try {
            return mapper.readValue(raw, type);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Redis JSON 파싱 실패", e);
        }
    }

    public <T> T readJson(String raw, TypeReference<T> typeRef) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Redis에 경로 데이터가 없습니다.");
        }
        try {
            return mapper.readValue(raw, typeRef);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Redis JSON 파싱 실패", e);
        }
    }



    private boolean hasDayOverlap(List<Boolean> a, List<Boolean> b) {
        int size = Math.min(a.size(), b.size());

        for (int i = 0; i < size; i++) {
            if (Boolean.TRUE.equals(a.get(i)) &&
                    Boolean.TRUE.equals(b.get(i))) {
                return true;
            }
        }
        return false;
    }
    private LocalTime getDepartureTime(int spareTime, LocalTime targetArrivalTime, int estimatedDurationMin) {
        return targetArrivalTime
                .minusMinutes(estimatedDurationMin)
                .minusMinutes(spareTime);
    }

    private long toBitMask(List<Boolean> list) {

        long mask = 0;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i)) {
                mask |= (1L << i);
            }
        }

        return mask;
    }

    private List<Boolean> fromBitMask(
            long mask,
            int size
    ) {

        List<Boolean> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            result.add((mask & (1L << i)) != 0);
        }

        return result;
    }


    private void saveRouteXY(List<OdsayPathDto> path ,
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


}
