package com.ruttu.project_02_backend.controller.auth;

import com.ruttu.project_02_backend.dto.auth.GoogleLoginRequestDto;
import com.ruttu.project_02_backend.dto.auth.LoginResponseDto;
import com.ruttu.project_02_backend.dto.auth.LogoutDto;
import com.ruttu.project_02_backend.dto.auth.RefreshTokenRequestDto;
import com.ruttu.project_02_backend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "인증 API")
public class AuthController {
    private final AuthService authService;
    //구글 로그인
    @PostMapping("/google")
    @Operation(
            summary = "구글 로그인",
            description = "구글 계정으로 간편로그인"
    )
    public ResponseEntity<LoginResponseDto> googleLogin(
            @RequestBody GoogleLoginRequestDto request
    ){
        System.out.println("🔥 API 들어옴");
        return ResponseEntity.ok(
                authService.googleLogin(request)
        );

    }

    //로그아웃(refresh token 무효화)
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> logout(@RequestBody LogoutDto logoutDto) {

        authService.logout(logoutDto.getRefreshToken());

        return ResponseEntity.ok("로그아웃 완료");
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "refresh token",
            description = "refresh token 테스트"
    )
    public ResponseEntity<LoginResponseDto> refresh(
            @RequestBody RefreshTokenRequestDto request
    ) {
        LoginResponseDto response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}