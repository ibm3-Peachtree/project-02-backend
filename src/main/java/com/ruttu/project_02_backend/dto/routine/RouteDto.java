package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {

    private int pathType;
    private int totalDistance;
    private int trafficDistance;
    private int totalWalk;
    private int totalTime;
    private int payment;
    private List<PathDto> path;
}
