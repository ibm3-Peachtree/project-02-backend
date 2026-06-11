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
    private Integer year;
    private Integer month;
    private LocalDate monthStartDate;
    private CommuteTimeMinDto avgCommuteTimeMin;
    private CommuteTimeMinDto maxCommuteTimeMin;
    private CommuteTimeMinDto minCommuteTimeMin;
    private Integer monthlyTransportCost;
    private Integer monthlyBurnedCalories;
    private Integer lateRiskCount;
    private Integer totalLateCount;
    private Integer changeRouteCount;
    private Double avgSatWaitTimeScore;
    private Double avgSatEtaScore;
    private Double avgSatRouteScore;
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
        this.setTotalLateCount(entity.getTotalLateCount());
        this.setChangeRouteCount(entity.getChangeRouteCount());
        this.setAvgSatWaitTimeScore(entity.getAvgSatWaitTimeScore());
        this.setAvgSatEtaScore(entity.getAvgSatEtaScore());
        this.setAvgSatRouteScore(entity.getAvgSatRouteScore());
        this.setRecommendedComfortTime(entity.getRecommendedComfortTime());
    }
}
