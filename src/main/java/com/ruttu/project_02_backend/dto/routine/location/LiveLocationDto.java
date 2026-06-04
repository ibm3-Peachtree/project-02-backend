package com.ruttu.project_02_backend.dto.routine.location;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LiveLocationDto {

    private String type;
    private double latitude;
    private double longitude;
    private double speed;
    private double accuracy;
}
