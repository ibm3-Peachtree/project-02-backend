package com.ruttu.project_02_backend.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenRequestDto {
    private String refreshToken;
}
