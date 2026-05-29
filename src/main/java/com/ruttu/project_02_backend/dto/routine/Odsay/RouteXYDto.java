package com.ruttu.project_02_backend.dto.routine.Odsay;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteXYDto {
    private String stationName;
    private Double x;
    private Double y;
    private String arsID;
    private String type; // walk, bus, subway

}