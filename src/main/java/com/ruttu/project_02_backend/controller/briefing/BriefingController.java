package com.ruttu.project_02_backend.controller.briefing;

import com.ruttu.project_02_backend.config.CustomUserDetails;
import com.ruttu.project_02_backend.dto.briefing.IncidentDto;
import com.ruttu.project_02_backend.service.briefing.BriefingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Briefing API", description = "브리핑 관리 API")
@RequiredArgsConstructor
@RequestMapping("/me/briefing")
//@SecurityRequirement(name="JWT")
@RestController
public class BriefingController {

    private final BriefingService briefingService;

    // 주요 교통 일정
    @GetMapping("/traffic")
    public ResponseEntity<Set<IncidentDto>> getTrafficSchedule(
    ){
        return ResponseEntity.ok(briefingService.getTrafficSchedule());
    }

    // 날씨

    // 일정


}
