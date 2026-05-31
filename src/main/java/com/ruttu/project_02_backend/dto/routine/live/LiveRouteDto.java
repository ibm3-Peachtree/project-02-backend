package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.Odsay.RouteDto;
import com.ruttu.project_02_backend.dto.routine.Odsay.RouteSectionDto;
import com.ruttu.project_02_backend.dto.routine.Odsay.TransitSectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveRouteDto {
    private int totalDistance;
    private int totalTime;
    private int payment;
    private String startName;
    private String endName;
    private List<RouteSectionDto> path;

    public LiveRouteDto(RouteDto routeDto) {
        this.totalDistance = routeDto.getTotalDistance();
        this.totalTime = routeDto.getTotalTime();
        this.payment = routeDto.getPayment();
        this.startName = routeDto.getStartName();
        this.endName = routeDto.getEndName();
        this.path = routeDto.getPath();
    }
}
