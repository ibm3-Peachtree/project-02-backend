package com.ruttu.project_02_backend.dto.report;

import java.time.LocalDate;

import com.ruttu.project_02_backend.entity.prod.report.UserMonthlyReportEntity;
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
    private Long userRoutineId;
    private int year;
    private int month;
    private LocalDate monthStartDate;
    private CommuteTimeMinDto avgCommuteTimeMin;
    private CommuteTimeMinDto maxCommuteTimeMin;
    private CommuteTimeMinDto minCommuteTimeMin;
    private int monthlyTransportCost;
    private int monthlyBurnedCalories;
    private int lateRiskCount;
    private ComfortTimeDto recommendedComfortTime;

    public MonthlyReportDto(UserMonthlyReportEntity entity){
        this.setId(entity.getId());
        this.setUserRoutineId(entity.getUserRoutineId());
        this.setYear(entity.getYear());
        this.setMonth(entity.getMonth());
        this.setMonthStartDate(entity.getMonthStartDate());
        this.setAvgCommuteTimeMin(entity.getAvgCommuteTimeMin());
        this.setMaxCommuteTimeMin(entity.getMaxCommuteTimeMin());
        this.setMinCommuteTimeMin(entity.getMinCommuteTimeMin());
        this.setMonthlyTransportCost(entity.getMonthlyTransportCost());
        this.setMonthlyBurnedCalories(entity.getMonthlyBurnedCalories());
        this.setLateRiskCount(entity.getLateRiskCount());
        this.setRecommendedComfortTime(entity.getRecommendedComfortTime());
    }
}
