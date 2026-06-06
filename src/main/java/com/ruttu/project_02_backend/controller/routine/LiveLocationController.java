package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import com.ruttu.project_02_backend.service.routine.LiveLocationService;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@SecurityRequirement(name="JWT")
@Tag(name = "Location API", description = "위치 관리 API")
@Controller
@RequiredArgsConstructor
public class LiveLocationController {

    private final LiveLocationService liveLocationService;
    private final LiveRouteService liveRouteService;

    @Operation(
            summary = "나의 경로 조회",
            description = "나의 경로 조회"
    )
    @MessageMapping("/location/my")
    public void myLocation(
            LiveLocationDto liveLocationDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUserId();
        String principalName = auth.getName();

        // 상태
        liveLocationService.sendRouteProgress(
                principalName, liveLocationDto
        );

        // 추가: incident 있으면 STOMP push
        liveRouteService.sendIncidentsDetour(userId, principalName);

        // 나의 경로
        liveLocationService.getMyCurrentSection(
                userId, principalName, liveLocationDto
        );

        // 저장
        liveLocationService.updateLocation(userId, liveLocationDto);

    }


    @MessageMapping("/location/reco")
    public void recoLocation(
            LiveLocationDto liveLocationDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUserId();
        String principalName = auth.getName();

        // 상태
        liveLocationService.sendRouteProgress(
                principalName, liveLocationDto
        );

        // 추가: incident 있으면 STOMP push
        liveRouteService.sendIncidentsDetour(userId, principalName);

        // 추천 경로
        liveLocationService.getRecoCurrentSection(
                userId, principalName, liveLocationDto
        );

        // 저장
        liveLocationService.updateLocation(userId, liveLocationDto);

    }

}
