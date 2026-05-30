package com.ruttu.project_02_backend.dto.user;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailAddressDto { // 상세 정보 dto

    private Long addressId;
    private String name;
    private String roadAddress;
    private String jibunAddress;
}
