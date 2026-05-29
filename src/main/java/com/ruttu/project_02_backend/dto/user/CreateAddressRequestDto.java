package com.ruttu.project_02_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAddressRequestDto {

    private String name;
    private String roadAddress;
    private String jibunAddress;

}
