package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDto {
    private LocalDateTime dateTime;
    private int tmp;
    private String wsd;
    private String sky;
    private int pop;
    private String pcp;
    private int reh;
    private String sno;
}
