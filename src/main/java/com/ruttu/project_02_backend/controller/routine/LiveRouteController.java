package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.routine.live.CurrentSectionDto;
import com.ruttu.project_02_backend.dto.routine.location.CurrentLocationDto;
import com.ruttu.project_02_backend.dto.routine.live.LiveRouteDto;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Live Routine API", description = "실시간 루틴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/me/routines/active")
@SecurityRequirement(name="JWT")
public class LiveRouteController {

    private final LiveRouteService liveRouteService;

    @Operation(
            summary = "현재 단계 조회",
            description = "도보중/대기중/탑승중 표시"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "단계 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrentLocationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "단계 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/status")
    public ResponseEntity<CurrentLocationDto> getRouteProgress(
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(liveRouteService.getRouteProgress(user.getUserId()));
    }


    @Operation(
            summary = "나의 경로 조회",
            description = "실시간 내 경로 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LiveRouteDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "경로 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/route")
    public ResponseEntity<LiveRouteDto> getMyRoute(
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(liveRouteService.getMyRoute(user.getUserId()));
    }


    @Operation(
            summary = "추천 경로 조회",
            description = "실시간 추천 경로 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LiveRouteDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "경로 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/reco")
    public ResponseEntity<LiveRouteDto> getRecommendedRoute(
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(liveRouteService.getRecommendedRoute(user.getUserId()));
    }

    @Operation(
            summary = "실시간 이동 구간 조회",
            description = "현재 위치한 경로 구간 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "구간 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrentSectionDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "구간 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/location")
    public ResponseEntity<CurrentSectionDto> getCurrentSection(
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(liveRouteService.getCurrentSection(user.getUserId()));
    }

    

}
