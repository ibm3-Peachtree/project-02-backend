package com.ruttu.project_02_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAddressResponseDto {

    private Long addressId;
    private String name;
    private String roadAddress;
    private String jibunAddress;
    private Double latitude;
    private Double longitude;
}
