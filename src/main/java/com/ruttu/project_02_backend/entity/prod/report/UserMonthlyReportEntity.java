package com.ruttu.project_02_backend.entity.prod.report;

import com.ruttu.project_02_backend.dto.report.ComfortTimeDto;
import com.ruttu.project_02_backend.dto.report.CommuteTimeMinDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "user_monthly_report")
public class UserMonthlyReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_routine_id", nullable = false)
    private Long userRoutineId;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "month_start_date", nullable = false)
    private LocalDate monthStartDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "avg_commute_time_min", nullable = false)
    private CommuteTimeMinDto avgCommuteTimeMin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "max_commute_time_min", nullable = false)
    private CommuteTimeMinDto maxCommuteTimeMin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "min_commute_time_min", nullable = false)
    private CommuteTimeMinDto minCommuteTimeMin;

    @Column(name = "monthly_transport_cost")
    private Integer monthlyTransportCost;

    @Column(name = "monthly_burned_calories")
    private Integer monthlyBurnedCalories;

    @Column(name = "late_risk_count")
    private Integer lateRiskCount;

    @Column(name = "total_late_count")
    private Integer totalLateCount;

    @Column(name = "change_route_count")
    private Integer changeRouteCount;

    @Column(name = "avg_sat_wait_time_score")
    private Double avgSatWaitTimeScore;

    @Column(name = "avg_sat_eta_score")
    private Double avgSatEtaScore;

    @Column(name = "avg_sat_route_score")
    private Double avgSatRouteScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommended_comfort_time", nullable = false)
    private ComfortTimeDto recommendedComfortTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;


}