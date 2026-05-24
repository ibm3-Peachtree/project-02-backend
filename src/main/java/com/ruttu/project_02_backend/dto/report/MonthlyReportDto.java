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
