package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWeatherDto {
    private double tmp; // 현재 기온
    private double minTemp; // 최저 기온
    private double maxTemp; // 최고 기온
    private String sky; // 하늘상태
    private String pcp; // 강수량
    private String pm10; // 미세먼지
    private String pm25; // 초미세먼지
    private String clothes; // 옷차림
    private String supplies; // 준비물

    public ResponseWeatherDto(TodayWeatherAirQualityDto weather, GeminiResultDto supplies){
        this.setTmp(weather.getTmp());
        this.setMinTemp(weather.getMinTemp());
        this.setMaxTemp(weather.getMaxTemp());
        this.setSky(weather.getSky());
        this.setPcp(weather.getPcp());
        this.setPm10(weather.getPm10());
        this.setPm25(weather.getPm25());
        this.setClothes(supplies.getClothes());
        this.setSupplies(supplies.getSupplies());
    }
}
