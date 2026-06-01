package com.ruttu.project_02_backend.dto.user;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyInfoDto {
    private String email;
    private String nickname;
}
