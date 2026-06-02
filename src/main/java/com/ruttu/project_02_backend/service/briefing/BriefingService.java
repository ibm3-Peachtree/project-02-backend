package com.ruttu.project_02_backend.service.briefing;

import com.ruttu.project_02_backend.dto.briefing.IncidentDto;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteDto;
import com.ruttu.project_02_backend.service.routine.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BriefingService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    private final RoutineService routineService;

    public Set<IncidentDto> getTrafficSchedule(){

        LocalDate today = LocalDate.now();
        String date = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        Set<String> keys = redisTemplate.keys(
                "incident:서울특별시:*:" + date + ":set"
        );

        Set<IncidentDto> allIncidents = keys.stream()
                .map(key -> redisTemplate.opsForSet().members(key))
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .map(obj -> {
                    try {
                        return mapper.readValue(
                                (String) obj,
                                IncidentDto.class
                        );
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        return allIncidents;
    }
}
