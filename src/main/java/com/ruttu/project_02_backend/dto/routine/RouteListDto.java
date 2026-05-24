package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteListDto {
    private int recoId;
    private List<String> trafficType;
    private int totalTime;
    private int payment;
}
