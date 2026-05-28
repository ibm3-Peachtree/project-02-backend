package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {
    private int recoId;
    private int totalDistance;
    private int totalTime;
    private int payment;
    private String startName;
    private String endName;
    private List<RouteSectionDto> path;
}
