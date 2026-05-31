package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.odsay.*;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteSectionDto;
import com.ruttu.project_02_backend.entity.prod.user.UserAddressEntity;
import com.ruttu.project_02_backend.exception.user.AddressNotFoundException;
import com.ruttu.project_02_backend.repository.prod.user.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OdsayIOService {
    @Value("${odsay.api.key}")
    private String apiKey;

    private final UserAddressRepository userAddressRepository;

    public OdsayResponseDto getOdsay(OdsayXYDto xy) throws Exception {

        String urlInfo =
                "https://api.odsay.com/v1/api/searchPubTransPathT" +
                        "?SX=" + xy.getSx() +
                        "&SY=" + xy.getSy() +
                        "&EX=" + xy.getEx() +
                        "&EY=" + xy.getEy() +
                        "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8");

        // URL 생성
        URL url = new URL(urlInfo);

        // HTTP 연결
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        // 응답 코드 확인
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode = " + responseCode);

        BufferedReader br;

        // 정상 응답 / 에러 응답 분기
        if (responseCode >= 200 && responseCode < 300) {
            br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        // 응답 읽기
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        conn.disconnect();

        ObjectMapper objectMapper = new ObjectMapper();

        OdsayResponseDto response =
                objectMapper.readValue(sb.toString(), OdsayResponseDto.class);

        return response;
    }

    public List<OdsayPathDto> getPath(OdsayResponseDto routes, int limitSize){
        return routes.getResult()
                .getPath()
                .stream()
                .limit(limitSize).toList();
    }

    public List<OdsayPathDto.Info> getInfo(List<OdsayPathDto> path){
        return path.stream()
                .map(OdsayPathDto::getInfo)
                .toList();

    }

    public List<List<OdsayPathDto.SubPath>> getSubPaths(List<OdsayPathDto> path){
        return path.stream()
                .map(OdsayPathDto::getSubPath)
                .toList();

    }

    public OdsayXYDto getOdsayXyById(Long originId, Long destinationId) {
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

    public OdsayXYDto getOdsayXyByAlias(Long userId, String originAlias, String destinationAlias) {
        UserAddressEntity origin = userAddressRepository
                .findByUserIdAndAlias(userId, originAlias);
        UserAddressEntity destination = userAddressRepository
                .findByUserIdAndAlias(userId, destinationAlias);

        OdsayXYDto xy = new OdsayXYDto();
        xy.setSx(origin.getLng());
        xy.setSy(origin.getLat());
        xy.setEx(destination.getLng());
        xy.setEy(destination.getLat());
        return xy;
    }

    public List<List<RouteXYDto>> getRouteXY(List<OdsayPathDto> path) {
        return path.stream()
                .map(p -> {
                    List<String> no = getTrafficTypeNo(Collections.singletonList(p.getSubPath())).getFirst();
                    List<OdsayPathDto.SubPath> subPaths = p.getSubPath();
                    return IntStream.range(0, subPaths.size())
                            .boxed()
                            .flatMap(i -> {
                                String type = trafficType2Eng(subPaths.get(i).getTrafficType());

                                if (type.equals("walk")) {
                                    return Stream.of(new RouteXYDto(null, null, null, null, "walk", no.get(i)));
                                }

                                return Optional.ofNullable(subPaths.get(i).getPassStopList())
                                        .map(pl -> pl.getStations())
                                        .orElse(Collections.emptyList())
                                        .stream()
                                        .map(s -> new RouteXYDto(
                                                s.getStationName(),
                                                s.getX(),
                                                s.getY(),
                                                s.getArsID(),
                                                type,
                                                no.get(i)
                                        ));
                            })
                            .toList();
                })
                .toList();
    }


    public List<List<RouteSectionDto>> getDetailPaths(
            List<List<OdsayPathDto.SubPath>> subPaths) {

        return subPaths.stream()
                .map(p -> p.stream()
                        .map(sp -> {

                            String type = trafficType2Eng(sp.getTrafficType());

                            return switch (type) {

                                case "walk" ->
                                        new WalkSectionDto(sp.getSectionTime(),
                                        List.of(""));

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
                                                  .map(lane -> String.valueOf(lane.getName()))
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

    public List<RouteDto> getDetailRoutes(
            List<List<OdsayPathDto.SubPath>> subPaths,
            List<OdsayPathDto.Info> infos
    ) {

        List<List<RouteSectionDto>> detailRoutes = getDetailPaths(subPaths);

        return IntStream.range(0, infos.size())
                .mapToObj(i -> new RouteDto(
                        i,
                        infos.get(i).getTotalDistance(),
                        infos.get(i).getTotalTime(),
                        infos.get(i).getPayment(),
                        infos.get(i).getFirstStartStation(),
                        infos.get(i).getLastEndStation(),
                        detailRoutes.get(i)
                ))
                .toList();
    }


    public List<String> extractStations(OdsayPathDto.SubPath sp) {
        if (sp.getPassStopList() == null) return List.of();

        return sp.getPassStopList().getStations().stream()
                .map(OdsayPathDto.Station::getStationName)
                .toList();
    }


    public List<List<String>> getTrafficTypeNo(List<List<OdsayPathDto.SubPath>> subPaths) {

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
                                                .getName()
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


    public String trafficType2Eng(int trafficType) {
        // 1-지하철, 2-버스, 3-도보
        if (trafficType == 2) {
            return "bus";

        } else if (trafficType == 1) {
            return "subway";

        } else {
            return "walk";
        }
    }
}
