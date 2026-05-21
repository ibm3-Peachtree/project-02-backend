package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.CurrentLocationDto;
import com.ruttu.project_02_backend.dto.routine.SpeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class LiveRouteService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CurrentLocationDto getRouteProgress(){
        Instant now = Instant.now();
        String raw = (String) redisTemplate.opsForValue().get("location:user:1");

        // 1. 싱글쿼트 → 더블쿼트
        String fixed = raw.replace("'", "\"");

        ObjectMapper mapper = new ObjectMapper();
        SpeedDto speed = mapper.readValue(fixed, SpeedDto.class);

        CurrentLocationDto currentLocationDto = new CurrentLocationDto();
        currentLocationDto.setStatus(getStatus(speed));
        currentLocationDto.setUpdatedAt(now.toEpochMilli());
        return currentLocationDto;
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
