package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.dto.routine.CurrentLocationDto;
import com.ruttu.project_02_backend.service.routine.LiveRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ResponseEntity<CurrentLocationDto> getRouteProgress(){

        return ResponseEntity.ok(liveRouteService.getRouteProgress());
    }


//    @Operation(
//            summary = "나의 경로 조회",
//            description = "Redis에서 GPS 기반 실시간 내 경로 조회"
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "경로 조회 성공",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = RouteDto.class)
//                    )
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "경로 조회 실패",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = Void.class)
//                    )
//            )
//    }
//    )
//    @GetMapping("/route")
//    public ResponseEntity<RouteDto> getMyRoute(){
//        return ResponseEntity.ok(liveRouteService.getMyRoute());
//    }
}
