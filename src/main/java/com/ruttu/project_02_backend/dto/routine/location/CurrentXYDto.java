package com.ruttu.project_02_backend.dto.routine.location;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CurrentXYDto {
    private Double latitude;
    private Double longitude;
    private Double accuracy;

    public CurrentXYDto(LiveLocationDto liveLocationDto) {
        this.setLatitude(liveLocationDto.getLatitude());
        this.setLongitude(liveLocationDto.getLongitude());
        this.setAccuracy(liveLocationDto.getAccuracy());
    }
}
