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
    private String TMP;
    private String WSD;
    private String SKY;
    private String POP;
    private String PCP;
    private String REH;
    private String SNO;
}
