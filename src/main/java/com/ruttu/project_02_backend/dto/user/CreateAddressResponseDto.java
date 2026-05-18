package com.ruttu.project_02_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAddressResponseDto {

    private Long addressId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
