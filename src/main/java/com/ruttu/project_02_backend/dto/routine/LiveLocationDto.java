package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LiveLocationDto {

    private double latitude;
    private double longitude;
    private double speed;
    private double accuracy;
}
