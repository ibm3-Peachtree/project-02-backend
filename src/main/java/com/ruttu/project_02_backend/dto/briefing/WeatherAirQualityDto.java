package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherAirQualityDto {
    private List<WeatherDto> weather;
    private List<AirQualityDto> airQuality;
}
