package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.routine.live.DetourDto;
import com.ruttu.project_02_backend.dto.routine.live.RoutineCompleteDto;
import com.ruttu.project_02_backend.dto.routine.live.LiveRouteDto;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteDto;
import com.ruttu.project_02_backend.dto.routine.routine.RouteListDto;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Live Routine API", description = "실시간 루틴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/me/routines/active")
@SecurityRequirement(name="JWT")
public class LiveRouteController {

    private final LiveRouteService liveRouteService;
    private static final Logger log =
            LoggerFactory.getLogger(LiveRouteService.class);

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
    @GetMapping("/route/{routineId}")
    public ResponseEntity<LiveRouteDto> getMyRoute(
            @PathVariable Long routineId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUserId();
        String principalName = auth.getName();

        LiveRouteDto result = liveRouteService.getMyRoute(userId, routineId);

        // STOMP push는 이후에 (실패해도 응답에 영향 없도록)
        try {
            liveRouteService.sendIncidentsDetour(userId, principalName);
        } catch (Exception e) {
            log.debug("sendIncidentsDetour 실패, 응답은 정상 반환. userId={}", userId);
        }

        return ResponseEntity.ok(result);
    }




    @Operation(
            summary = "추천 경로 목록 조회",
            description = "실시간 추천 경로 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = RouteListDto.class)
                            )                    )
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
    @GetMapping("/reco/{routineId}")
    public ResponseEntity<List<RouteListDto>> getRecommendedRoute(
            @PathVariable Long routineId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUserId();
        String principalName = auth.getName();

        // 경로 먼저 조회
        List<RouteListDto> result = liveRouteService.getRecommendedRoute(userId, routineId);

        // STOMP push는 이후에 (실패해도 응답에 영향 없도록)
        try {
            liveRouteService.sendIncidentsDetour(userId, principalName);
        } catch (Exception e) {
            log.debug("sendIncidentsDetour 실패, 응답은 정상 반환. userId={}", userId);
        }

        return ResponseEntity.ok(result);
    }



    @Operation(
            summary = "추천 경로 상세 조회",
            description = "추천 경로 상세 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RouteDto.class)
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
    @GetMapping("/reco/detail/{recoId}")
    public ResponseEntity<RouteDto> getRecommendedRouteDetail(
            @PathVariable int recoId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(liveRouteService.getRecommendedRouteDetail(user.getUserId(), recoId));
    }


    @Operation(
            summary = "추천 우회 경로 상세 조회",
            description = "추천 우회 경로 상세 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DetourDto.class)
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
    @GetMapping("/reco/detour/{pathId}")
    public ResponseEntity<DetourDto> getDetour(
            @PathVariable int pathId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(liveRouteService.getDetour(user.getUserId(), pathId));
    }


    @Operation(
            summary = "추천 경로 저장",
            description = "실시간 추천 경로 Redis에 저장(이 경로로 변경 클릭 시)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "경로 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @PostMapping("/reco/detail/{recoId}")
    public ResponseEntity<Void> saveRecommendedRoute(
            @PathVariable int recoId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        liveRouteService.saveRecommendedRoute(user.getUserId(), recoId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "추천 우회 경로 저장",
            description = "실시간 추천 우회 경로 Redis에 저장(이 경로로 변경 클릭 시)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "경로 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @PostMapping("/reco/detour/{pathId}")
    public ResponseEntity<Void> saveDetourRoute(
            @PathVariable int pathId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        liveRouteService.saveDetourRoute(user.getUserId(), pathId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(
            summary = "실시간 경로 안내 종료(나의 경로)",
            description = "실시간 경로 안내 종료"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @PostMapping("/complete/my")
    public ResponseEntity<Void> myRoutecompleted(
            @RequestBody RoutineCompleteDto routineCompleteDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        liveRouteService.myRoutecompleted(user.getUserId(),routineCompleteDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }






    @Operation(
            summary = "실시간 경로 안내 종료(추천 경로)",
            description = "실시간 경로 안내 종료"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @PostMapping("/complete/reco")
    public ResponseEntity<Void> recoRoutecompleted(
            @RequestBody RoutineCompleteDto routineCompleteDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        liveRouteService.recoRoutecompleted(user.getUserId(),routineCompleteDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
