package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.routine.Odsay.RouteDto;
import com.ruttu.project_02_backend.dto.routine.routine.RouteListDto;
import com.ruttu.project_02_backend.dto.routine.routine.RoutineDetailDto;
import com.ruttu.project_02_backend.dto.routine.routine.RoutineDto;
import com.ruttu.project_02_backend.dto.routine.routine.RoutineListDto;
import com.ruttu.project_02_backend.service.routine.RoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Routine API", description = "루틴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/me/routines")
@SecurityRequirement(name="JWT")
public class RoutineController {

    private final RoutineService routineService;


    @Operation(
            summary = "루틴 생성",
            description = "루틴 생성"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "루틴 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "동일 시간대 루틴 존재",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @PostMapping
    public ResponseEntity<Void> createRoutine(
            @RequestBody RoutineDto routineDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        routineService.createRoutine(routineDto, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(
            summary = "루틴 삭제",
            description = "루틴 삭제"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "루틴 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "루틴 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> deleteRoutine(
            @PathVariable Long routineId
    ){
        routineService.deleteRoutine(routineId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(
            summary = "루틴 수정",
            description = "루틴 수정"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "루틴 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "루틴 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @PutMapping("/{routineId}")
    public ResponseEntity<Void> updateRoutine(
            @PathVariable Long routineId,
            @RequestBody RoutineDto routineDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        routineService.updateRoutine(routineId, routineDto, user.getUserId());
       return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(
            summary = "루틴 목록 조회",
            description = "루틴 목록 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "루틴 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = RoutineListDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "루틴 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    })
    @GetMapping()
    public ResponseEntity<List<RoutineListDto>> getRoutine(
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(routineService.getRoutine(user.getUserId()));
    }


    @Operation(
            summary = "루틴 상세 조회",
            description = "루틴 상세 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "루틴 상세 조회",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoutineDetailDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "루틴 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/{routineId}")
    public ResponseEntity<RoutineDetailDto> getRoutineDetail(
            @PathVariable Long routineId
    ){
        return ResponseEntity.ok(routineService.getRoutineDetail(routineId));
    }


    @Operation(
            summary = "상세 경로 조회",
            description = "상세 경로 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상세 경로 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RouteDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "상세 경로 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/routes/recommend/{recoId}")
    public ResponseEntity<RouteDto> getRouteDetail(
            @PathVariable int recoId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(routineService.getRouteDetail(recoId, user.getUserId()));
    }


    @Operation(
            summary = "경로 목록 조회",
            description = "간략한 환승정보 및 소요시간, 요금 정보 제공"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = RouteListDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "경로 목록 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/routes/recommend")
    public ResponseEntity<List<RouteListDto>> getRoute(
            @RequestParam Long originId,
            @RequestParam Long destinationId,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(routineService.getRoute(originId, destinationId, user.getUserId()));
    }
}
