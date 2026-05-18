package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirQualityDto {

    private AirQualityRegionDto pm10;
    private AirQualityRegionDto pm25;
}
