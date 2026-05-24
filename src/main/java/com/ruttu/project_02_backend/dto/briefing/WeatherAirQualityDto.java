package com.ruttu.project_02_backend.dto.briefing;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherAirQualityDto {
    private List<WeatherDto> weather;
    private List<AirQualityDto> airQuality;
}
