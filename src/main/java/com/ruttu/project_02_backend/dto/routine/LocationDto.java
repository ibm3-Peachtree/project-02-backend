package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocationDto {

    private double lat;
    private double lng;
    private double accuracy;
    private double speed;
}
