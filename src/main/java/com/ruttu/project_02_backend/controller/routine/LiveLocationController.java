package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import com.ruttu.project_02_backend.service.routine.LiveLocationService;
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
@RequestMapping("/me/routines/location")
@RequiredArgsConstructor
public class LiveLocationController {

    private final LiveLocationService liveLocationService;
    @Operation(
            summary = "나의 경로 조회",
            description = "나의 경로 조회"
    )
    @MessageMapping("/my")
    public void myLocation(
            LiveLocationDto liveLocationDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUserId();

        // 상태
        liveLocationService.sendRouteProgress(
                userId, liveLocationDto
        );

        // 나의 경로
        liveLocationService.getMyCurrentSection(
                userId, liveLocationDto
        );

        // 저장
        liveLocationService.updateLocation(userId, liveLocationDto);

    }


    @MessageMapping("/reco")
    public void recoLocation(
            LiveLocationDto liveLocationDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUserId();

        // 상태
        liveLocationService.sendRouteProgress(
                userId, liveLocationDto
        );

        // 추천 경로
        liveLocationService.getRecoCurrentSection(
                userId, liveLocationDto
        );

        // 저장
        liveLocationService.updateLocation(userId, liveLocationDto);

    }

}
