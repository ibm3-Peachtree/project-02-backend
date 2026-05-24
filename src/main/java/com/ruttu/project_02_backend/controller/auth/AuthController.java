package com.ruttu.project_02_backend.controller.auth;

import com.ruttu.project_02_backend.config.JwtUtil;
import com.ruttu.project_02_backend.dto.auth.GoogleLoginRequestDto;
import com.ruttu.project_02_backend.dto.auth.LoginResponseDto;
import com.ruttu.project_02_backend.dto.auth.LogoutDto;
import com.ruttu.project_02_backend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    //구글 로그인
    @PostMapping("/google")
    @Operation(
            summary = "구글 로그인",
            description = "구글 계정으로 간편로그인"
    )
    public ResponseEntity<LoginResponseDto> googleLogin(
            @RequestBody GoogleLoginRequestDto request
    ){
            return ResponseEntity.ok(
                    authService.googleLogin(request)
            );

    }
    //로그아웃(refresh token 무효화)
    @PostMapping("/logout")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> logout(@RequestBody LogoutDto logoutDto) {

        authService.logout(logoutDto.getRefreshToken());

        return ResponseEntity.ok("로그아웃 완료");
    }
}
