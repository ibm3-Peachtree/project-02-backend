package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.LiveLocationDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class LiveLocationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;


    @Transactional
    public void updateLocation(Long userId, LiveLocationDto liveLocationDto) {

        String key = "location:user:" + userId + ":" + userId;
        String json = mapper.writeValueAsString(liveLocationDto);
        redisTemplate.opsForValue().set(key, json);
        System.out.println("saved " + key);

    }
}
