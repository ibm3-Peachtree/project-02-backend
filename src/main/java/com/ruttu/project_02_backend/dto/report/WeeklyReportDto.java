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
    private int year;
    private int weekOfYear;
    private LocalDate weekStartDate;
    private int avgCommuteTimeMin;
    private int weeklyTransportCost;
    private int weeklyBurnedCalories;
    private int lateRiskCount;
    private int avgWaitTimeMin;
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
        this.setAvgWaitTimeMin(entity.getAvgWaitTimeMin());
        this.setDaily(entity.getDaily());
    }

}
