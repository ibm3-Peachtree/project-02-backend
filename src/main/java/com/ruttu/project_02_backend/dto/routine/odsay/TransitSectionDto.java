package com.ruttu.project_02_backend.dto.routine.odsay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class TransitSectionDto implements RouteSectionDto {

    private int sectionTime;
    private List<String> no;
    private String start;
    private String end;
    private Integer stationCount;
    private List<String> stationName;
}
