package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.dto.routine.CurrentLocationDto;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Live Routine API", description = "실시간 루틴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/me/routines/active")
public class LiveRouteController {

    private final LiveRouteService liveRouteService;

    @GetMapping("/status")
    public ResponseEntity<CurrentLocationDto> getRouteProgress(){

        return ResponseEntity.ok(liveRouteService.getRouteProgress());
    }
}
