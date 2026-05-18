package com.ruttu.project_02_backend.service.routine;

import com.ruttu.project_02_backend.dto.routine.CurrentLocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class LiveRouteService {

    public CurrentLocationDto getRouteProgress(){
        Instant now = Instant.now();

        CurrentLocationDto tmp = new CurrentLocationDto();
        tmp.setStatus("도보중");
        tmp.setUpdatedAt(now.toEpochMilli());
        return tmp;
    }
}
