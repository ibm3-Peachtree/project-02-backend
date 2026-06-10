package com.ruttu.project_02_backend.dto.routine.live;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentsDto {
    private String incident_id;
    private String incident;
    private String start;
    private String end;

}
