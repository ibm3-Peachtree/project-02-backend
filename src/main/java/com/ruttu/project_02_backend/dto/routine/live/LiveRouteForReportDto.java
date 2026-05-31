package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.odsay.RouteSectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveRouteForReportDto {
    private Long routineId;
    private int totalDistance;
    private int totalTime;
    private int payment;
    private String startName;
    private String endName;
    private List<RouteSectionDto> path;

    public LiveRouteForReportDto(Long id, LiveRouteDto liveRoute) {
        this.setRoutineId(id);
        this.setTotalDistance(liveRoute.getTotalDistance());
        this.setTotalTime(liveRoute.getTotalTime());
        this.setPayment(liveRoute.getPayment());
        this.setStartName(liveRoute.getStartName());
        this.setEndName(liveRoute.getEndName());
        this.setPath(liveRoute.getPath());
    }
}
