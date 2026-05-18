package com.ruttu.project_02_backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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
