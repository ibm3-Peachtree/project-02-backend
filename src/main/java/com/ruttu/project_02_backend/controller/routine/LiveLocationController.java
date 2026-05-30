package com.ruttu.project_02_backend.controller.routine;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import com.ruttu.project_02_backend.service.routine.LiveLocationService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name="JWT")
@Tag(name = "Location API", description = "위치 관리 API")
@RestController
@RequestMapping("/me/routines/active")
@RequiredArgsConstructor
public class LiveLocationController {

    private final LiveLocationService liveLocationService;
    @Operation(
            summary = "위치 등록",
            description = "위치 등록"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "위치 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LiveLocationDto.class)
                    )
            )
    }
    )
    @PatchMapping
    public ResponseEntity<Void> updateLocation(
            @RequestBody LiveLocationDto liveLocationDto,
            Authentication auth
    ) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        liveLocationService.updateLocation(user.getUserId(), liveLocationDto);
        return ResponseEntity.ok().build();
    }
}
