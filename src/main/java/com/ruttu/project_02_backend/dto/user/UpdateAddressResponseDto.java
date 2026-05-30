package com.ruttu.project_02_backend.dto.user;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAddressResponseDto { // 주소 수정 후 dto
    private Long addressId;
    private String name;
    private String roadAddress;
    private String jibunAddress;
    private Double latitude;
    private Double longitude;
}
