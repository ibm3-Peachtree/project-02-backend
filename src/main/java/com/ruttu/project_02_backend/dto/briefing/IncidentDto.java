package com.ruttu.project_02_backend.dto.briefing;

import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
public class IncidentDto {
    private String si;
    private String gu;
    private String info;
    private double x;
    private double y;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double lat;
    private double lng;


}
