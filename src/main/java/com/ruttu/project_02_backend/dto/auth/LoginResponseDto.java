package com.ruttu.project_02_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String status;

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private String nickname;
}
