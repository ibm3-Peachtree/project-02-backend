package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.location.LiveLocationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CurrentXYDto {
    private Double latitude;
    private Double longitude;

    public CurrentXYDto(LiveLocationDto liveLocationDto) {
        this.setLatitude(liveLocationDto.getLatitude());
        this.setLongitude(liveLocationDto.getLongitude());
    }
}
