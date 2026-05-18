package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
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
