package com.ruttu.project_02_backend.dto.report;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyReportDto {
    private Long id;
    private int year;
    private int weekOfYear;
    private LocalDate weekStartDate;
    private int avgCommuteTimeMin;
    private int weeklyTransportCost;
    private int weeklyBurnedCalories;
    private int lateRiskCount;
    private int avgWaitTimeMin;
    private DailyDto daily;

}
