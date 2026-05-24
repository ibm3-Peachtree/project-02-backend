package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AirQualityDto {

    private AirQualityRegionDto pm10;
    private AirQualityRegionDto pm25;
}
