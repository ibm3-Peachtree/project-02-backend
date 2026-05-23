package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.*;
import com.ruttu.project_02_backend.entity.routine.UserRoutineEntity;
import com.ruttu.project_02_backend.entity.user.UserAddressEntity;
import com.ruttu.project_02_backend.exception.routine.DuplicateRoutineTargetArrivalTimeException;
import com.ruttu.project_02_backend.exception.routine.RoutineNotFoundException;
import com.ruttu.project_02_backend.exception.user.AddressNotFoundException;
import com.ruttu.project_02_backend.repository.routine.UserRoutineRepository;
import com.ruttu.project_02_backend.repository.user.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final UserRoutineRepository userRoutineRepository;
    private final UserAddressRepository userAddressRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final OdsayIOService odsayIOService;
    private final ObjectMapper mapper;

    // 내 루틴 생성
    @Transactional
    public void createRoutine(RoutineDto routineDto, Long userId) {

        boolean overlap = userRoutineRepository
                .findAllByTargetArrivalTimeAndUserId(
                        routineDto.getTargetArrivalTime(),
                        userId
                )
                .stream()
                .anyMatch(s ->
                        hasDayOverlap(
                                fromBitMask(s.getPreferredDowMask(),7), // 기존 DB
                                routineDto.getDow()                   // 요청
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

        saveUserRoutineEntity(routine, routineDto, userId);
    }

    // 내 루틴 조회
    @Transactional(readOnly = true)
    public List<RoutineListDto> getRoutine() {

        List<UserRoutineEntity> routines = userRoutineRepository.findAll();

        if (routines.isEmpty()) {
            throw new RoutineNotFoundException();
        }

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
        OdsayXYDto xy = getOdsayXy(originId, destinationId);

        // odsay 경로 조회
        try {

            OdsayResponseDto routes = odsayIOService.getOdsay(xy);

            List<OdsayPathDto> path = getPath(routes); // pathType: 1-지하철, 2-버스, 3-버스+지하철
            List<OdsayPathDto.Info> infos = getInfo(path);
            List<List<OdsayPathDto.SubPath>> subPaths = getSubPaths(path);


            // 상세보기 버전 생성하여 Redis에 넣기
            saveDetailRoutes(subPaths, infos, userId);

            // x,y 좌표 저장
            saveRouteXY(path, userId);

            List<List<String>> trafficTypes = getTrafficTypeNo(subPaths);
            return IntStream.range(0, infos.size())
                    .mapToObj(i -> {

                                RouteListDto resRouteList = new RouteListDto(
                                        i,
                                        trafficTypes.get(i),
                                        infos.get(i).getTotalTime(),
                                        infos.get(i).getPayment()
                                );

                                // redis에 저장
                                String key = "routine:route:summary:user:" + userId + ":" + i;

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
        LocalTime departureTime = getDepartureTime(  // 추천 출발 시간
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
    private <T> T readJson(String raw, Class<T> type) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Redis에 경로 데이터가 없습니다.");
        }
        try {
            return mapper.readValue(raw, type);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Redis JSON 파싱 실패", e);
        }
    }

    private <T> T readJson(String raw, TypeReference<T> typeRef) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("Redis에 경로 데이터가 없습니다.");
        }
        try {
            return mapper.readValue(raw, typeRef);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Redis JSON 파싱 실패", e);
        }
    }

    private List<OdsayPathDto> getPath(OdsayResponseDto routes){
        return routes.getResult()
        .getPath()
        .stream()
        .limit(5).toList();
    }

    private List<OdsayPathDto.Info> getInfo(List<OdsayPathDto> path){
        return path.stream()
        .map(OdsayPathDto::getInfo)
        .toList();

    }

    private List<List<OdsayPathDto.SubPath>> getSubPaths(List<OdsayPathDto> path){
        return path.stream()
        .map(OdsayPathDto::getSubPath)
        .toList();

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
    private LocalTime getDepartureTime(LocalTime targetArrivalTime, int estimatedDurationMin) {
        return targetArrivalTime
                .minusMinutes(estimatedDurationMin)
                .minusMinutes(15);
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

        List<List<RouteXYDto>> stations =
                path.stream()
                        .map(p -> p.getSubPath().stream()
                                .flatMap(sp -> {
                                    String type = trafficType2Eng(sp.getTrafficType());

                                    if (sp.getTrafficType() == 3) {
                                        return Stream.of(new RouteXYDto(null, null, null, null, "walk"));
                                    }

                                    return Optional.ofNullable(sp.getPassStopList())
                                            .map(pl -> pl.getStations())
                                            .orElse(Collections.emptyList())
                                            .stream()
                                            .map(s -> new RouteXYDto(
                                                    s.getStationName(),
                                                    s.getX(),
                                                    s.getY(),
                                                    s.getArsID(),
                                                    type
                                            ));
                                })
                                .toList()
                        )
                        .toList();


        IntStream.range(0, stations.size())
                .forEach(i -> {
                    String key = "routine:route:xy:user:" + userId + ":" + i;
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
        List<List<RouteSectionDto>> detailRoutes = getDetailPaths(subPaths);
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

                    String key = "routine:route:full:user:" + userId + ":" + i;
                    String json = mapper.writeValueAsString(routeDto);
                    redisTemplate.opsForValue().set(key, json);
                    System.out.println("saved " + key);

                });
    }

    private List<List<RouteSectionDto>> getDetailPaths(
            List<List<OdsayPathDto.SubPath>> subPaths) {

        return subPaths.stream()
                .map(p -> p.stream()
                        .map(sp -> {

                            String type = trafficType2Eng(sp.getTrafficType());

                            return switch (type) {

                                case "walk" ->
                                        new WalkSectionDto(sp.getSectionTime());

                                case "bus" -> new BusSectionDto(
                                        sp.getSectionTime(),

                                        sp.getLane() == null
                                                ? List.of()
                                                : sp.getLane().stream()
                                                  .map(OdsayPathDto.SubPath.Lane::getBusNo)
                                                  .toList(),

                                        sp.getStartName(),
                                        sp.getEndName(),
                                        sp.getStationCount(),
                                        extractStations(sp)
                                );

                                case "subway" -> new SubwaySectionDto(
                                        sp.getSectionTime(),

                                        sp.getLane() == null
                                                ? List.of()
                                                : sp.getLane().stream()
                                                  .map(lane -> String.valueOf(lane.getSubwayCode()))
                                                  .toList(),

                                        sp.getStartName(),
                                        sp.getEndName(),
                                        sp.getStationCount(),
                                        extractStations(sp),
                                        sp.getWay()
                                );

                                default ->
                                        throw new IllegalStateException("Unknown type: " + type);
                            };
                        })
                        .toList()
                )
                .toList();
    }

    private List<String> extractStations(OdsayPathDto.SubPath sp) {
        if (sp.getPassStopList() == null) return List.of();

        return sp.getPassStopList().getStations().stream()
                .map(OdsayPathDto.Station::getStationName)
                .toList();
    }
    private String trafficType2Eng(int trafficType) {
        // 1-지하철, 2-버스, 3-도보
        if (trafficType == 2) {
            return "bus";

        } else if (trafficType == 1) {
            return "subway";

        } else {
            return "walk";
        }
    }

    private List<List<String>> getTrafficTypeNo(List<List<OdsayPathDto.SubPath>> subPaths) {

        return subPaths.stream()
                .map(p -> p.stream()
                        .map(sp -> {

                            // 1-지하철, 2-버스, 3-도보
                            String trafficTypeEng = trafficType2Eng(sp.getTrafficType());
                            String no;

                            if (trafficTypeEng.equals("bus")) {
                                no = ":" + sp.getLane()
                                        .getFirst()
                                        .getBusNo();
                            } else if (trafficTypeEng.equals("subway")) {
                                no = ":" + String.valueOf(
                                        sp.getLane()
                                                .getFirst()
                                                .getSubwayCode()
                                );
                            } else {
                                no = "";
                            }
                            return trafficTypeEng + no;

                        })
                        .toList()
                )
                .toList();
    }

    private OdsayXYDto getOdsayXy(Long originId, Long destinationId) {
        UserAddressEntity origin = userAddressRepository.findById(originId)
                .orElseThrow(AddressNotFoundException::new);
        UserAddressEntity destination = userAddressRepository.findById(destinationId)
                .orElseThrow(AddressNotFoundException::new);

        OdsayXYDto xy = new OdsayXYDto();
        xy.setSx(origin.getLng());
        xy.setSy(origin.getLat());
        xy.setEx(destination.getLng());
        xy.setEy(destination.getLat());
        return xy;
    }


}
