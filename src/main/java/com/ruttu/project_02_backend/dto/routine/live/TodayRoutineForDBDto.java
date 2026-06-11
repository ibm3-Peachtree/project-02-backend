package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.odsay.RouteXYDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodayRoutineForDBDto {
    private Long userId;
    private Long userRoutineId;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private List<RouteXYDto> todayRoutine;
    private int transportCost;
    private int totalDistanceMeter;
    private int estimatedCalories;
    private boolean isLate;
    private boolean startedLate;
    private boolean isRouteFollowed;
    private boolean isComfort;
    private Integer satWaitTimeScore;
    private Integer satEtaScore;
    private Integer satRouteScore;
    private LocalDate date;
}
