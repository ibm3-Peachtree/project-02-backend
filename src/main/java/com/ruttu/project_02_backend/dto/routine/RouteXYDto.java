package com.ruttu.project_02_backend.dto.routine;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteXYDto {
    private String stationName;
    private Double x;
    private Double y;
    private String arsID;
    private String type;

}