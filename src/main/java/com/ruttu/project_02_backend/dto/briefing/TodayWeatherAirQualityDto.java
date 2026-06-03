package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodayWeatherAirQualityDto {
    private double tmp;
    private double minTemp;
    private double maxTemp;
    private String sky;
    private String pcp;
    private String pm10;
    private String pm25;
}
