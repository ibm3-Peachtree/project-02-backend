package com.ruttu.project_02_backend.entity.prod.report;

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
@Table(name = "user_weekly_report")
public class UserWeeklyReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "week_of_year", nullable = false)
    private Integer weekOfYear;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "avg_commute_time_min")
    private Integer avgCommuteTimeMin;

    @Column(name = "weekly_transport_cost")
    private Integer weeklyTransportCost;

    @Column(name = "weekly_burned_calories")
    private Integer weeklyBurnedCalories;

    @Column(name = "late_risk_count")
    private Integer lateRiskCount;

    @Column(name = "avg_wait_time_min")
    private Integer avgWaitTimeMin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "daily", nullable = false)
    private Map<String, Object> daily;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;


}