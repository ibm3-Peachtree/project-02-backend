package com.ruttu.project_02_backend.dto.report;

import java.time.LocalDate;

import com.ruttu.project_02_backend.entity.prod.report.UserWeeklyReportEntity;
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
    private Long userRoutineId;
    private Integer year;
    private Integer weekOfYear;
    private LocalDate weekStartDate;
    private Integer avgCommuteTimeMin;
    private Integer weeklyTransportCost;
    private Integer weeklyBurnedCalories;
    private Integer lateRiskCount;
    private Integer totalLateCount;
    private Integer changeRouteCount;
    private Double avgSatWaitTimeScore;
    private Double avgSatEtaScore;
    private Double avgSatRouteScore;
    private Integer avgWaitTimeMin;
    private DailyDto daily;

    public WeeklyReportDto(UserWeeklyReportEntity entity){
        this.setId(entity.getId());
        this.setId(entity.getUserRoutineId());
        this.setYear(entity.getYear());
        this.setWeekOfYear(entity.getWeekOfYear());
        this.setAvgCommuteTimeMin(entity.getAvgCommuteTimeMin());
        this.setWeeklyTransportCost(entity.getWeeklyTransportCost());
        this.setWeeklyBurnedCalories(entity.getWeeklyBurnedCalories());
        this.setLateRiskCount(entity.getLateRiskCount());
        this.setTotalLateCount(entity.getTotalLateCount());
        this.setChangeRouteCount(entity.getChangeRouteCount());
        this.setAvgSatWaitTimeScore(entity.getAvgSatWaitTimeScore());
        this.setAvgSatEtaScore(entity.getAvgSatEtaScore());
        this.setAvgSatRouteScore(entity.getAvgSatRouteScore());
        this.setAvgWaitTimeMin(entity.getAvgWaitTimeMin());
        this.setDaily(entity.getDaily());
    }

}
