package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutineDto {

    private String routineName;
    private String targetArrivalTime;
    private Long departureAddressId;
    private Long arrivalAddressId;
    private Long routeId;
    private List<String> days;
}
