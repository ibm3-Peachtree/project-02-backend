package com.ruttu.project_02_backend.controller.briefing;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.briefing.CalendarItemsDto;
import com.ruttu.project_02_backend.dto.briefing.ResponseWeatherDto;
import com.ruttu.project_02_backend.dto.routine.live.LiveRouteDto;
import com.ruttu.project_02_backend.dto.routine.routine.RouteListDto;
import com.ruttu.project_02_backend.service.briefing.BriefingService;
import com.ruttu.project_02_backend.service.briefing.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Tag(name = "Briefing API", description = "브리핑 관리 API")
@RequiredArgsConstructor
@RequestMapping("/me/briefing")
@SecurityRequirement(name="JWT")
@RestController
public class BriefingController {

    private final BriefingService briefingService;
    private final CalendarService calendarService;

    // 날씨
    @Operation(
            summary = "날씨 조회",
            description = "날씨, 미세먼지, 준비물 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "날씨 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseWeatherDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "날씨 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/weather")
    public ResponseEntity<ResponseWeatherDto> getWeather(
            Authentication auth
    ){
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(briefingService.getWeather(user.getUserId()));
    }

    // 일정
    @Operation(
            summary = "일정 조회",
            description = "일정 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = CalendarItemsDto.class)
                            )         )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "일정 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarItemsDto>> getCalendar(
            Authentication auth
    ){
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(calendarService.getCalendar(user.getUserId()));
    }

}
