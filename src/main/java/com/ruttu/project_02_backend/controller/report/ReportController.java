package com.ruttu.project_02_backend.controller.report;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.report.MonthlyReportDto;
import com.ruttu.project_02_backend.dto.report.WeeklyReportDto;
import com.ruttu.project_02_backend.service.report.ReportService;
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

@Tag(name = "Report API", description = "리포트 관리 API")
@RequiredArgsConstructor
@RequestMapping("/me/reports")
@SecurityRequirement(name="JWT")
@RestController
public class ReportController {

    private final ReportService reportService;

    // 주간
    @Operation(
            summary = "주간 리포트 조회",
            description = "주간 리포트 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주간 리포트 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = WeeklyReportDto.class)
                            )         )                    ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주간 리포트 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyReportDto>> getWeeklyReport(Authentication auth){
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        return ResponseEntity.ok(reportService.getWeeklyReport(user.getUserId()));
    }

    // 월간
    @Operation(
            summary = "월간 리포트 조회",
            description = "월간 리포트 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "월간 리포트 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = MonthlyReportDto.class)
                            )         )                    ),
            @ApiResponse(
                    responseCode = "404",
                    description = "월간 리포트 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            )
    }
    )
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportDto>> getMonthlyReport(Authentication auth){
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        return ResponseEntity.ok(reportService.getMonthlyReport(user.getUserId()));
    }
}
