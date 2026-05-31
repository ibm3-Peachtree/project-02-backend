package com.ruttu.project_02_backend.dto.routine.odsay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class PathDto {

    private int trafficType;
    private int distance;
    private int sectionTime;
    private Optional<Integer> stationCount;
    private Optional<Integer> subwayCode;
    private Optional<Integer> busNo;
    private Optional<String> startName;
    private Optional<String> endName;

}
