package com.ruttu.project_02_backend.dto.routine.odsay;

import com.ruttu.project_02_backend.dto.routine.live.DetourDto;
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
    private Integer payment;
    private String startName;
    private String endName;
    private List<RouteSectionDto> path;


}
