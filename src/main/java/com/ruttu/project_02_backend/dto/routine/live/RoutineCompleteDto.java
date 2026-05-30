package com.ruttu.project_02_backend.dto.routine.live;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineCompleteDto {
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Integer satWaitTimeScore;
    private Integer satEtaScore;
    private Integer satRouteScore;
}