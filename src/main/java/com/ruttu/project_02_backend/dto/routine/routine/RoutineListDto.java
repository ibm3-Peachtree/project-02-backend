package com.ruttu.project_02_backend.dto.routine.routine;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class RoutineListDto {
    private Long routineId;
    private String routineName;
    private String originAlias;
    private String destinationAlias;
    private List<Boolean> dow;
    private LocalTime targetArrivalTime;
    private LocalTime recommendedDepartureTime;
    private int estimatedDuration;
}
