package com.ruttu.project_02_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAddressDto { // 주소 수정 dto

    private String name;
    private String roadAddress;
    private String jibunAddress;
}
