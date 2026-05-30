package com.ruttu.project_02_backend.dto.routine.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoutineDto {

    private String routineName;
    private List<Boolean> dow;
    private LocalTime targetArrivalTime;
    private String originAlias;
    private String origin;
    private String destinationAlias;
    private String destination;
    private int recoId;
}
