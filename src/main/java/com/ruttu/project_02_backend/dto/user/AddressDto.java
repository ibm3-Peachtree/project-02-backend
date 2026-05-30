package com.ruttu.project_02_backend.dto.user;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto { // 주소 목록 dto

    private Long addressId;
    private String name;
    private String roadAddress;
}
