package com.ruttu.project_02_backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyDto {

    private DailyStatusDto mon;
    private DailyStatusDto tue;
    private DailyStatusDto wed;
    private DailyStatusDto thu;
    private DailyStatusDto fri;
    private DailyStatusDto sat;
    private DailyStatusDto sun;

}
