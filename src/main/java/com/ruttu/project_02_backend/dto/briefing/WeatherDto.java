package com.ruttu.project_02_backend.dto.briefing;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
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
