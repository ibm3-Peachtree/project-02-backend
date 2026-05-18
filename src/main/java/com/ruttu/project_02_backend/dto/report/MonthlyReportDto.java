package com.ruttu.project_02_backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReportDto {
    private Long id;
    private int year;
    private int month;
    private LocalDate monthStartDate;
    private int avgCommuteTimeMin;
    private int maxCommuteTimeMin;
    private int minCommuteTimeMin;
    private int monthlyTransportCost;
    private int monthlyBurnedCalories;
    private int lateRiskCount;
    private CommuteTimeMinDto recommendedComfortTime;

}
