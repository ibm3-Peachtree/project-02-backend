package com.ruttu.project_02_backend.entity.stats;

import com.ruttu.project_02_backend.dto.routine.odsay.RouteXYDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        schema = "stats",
        name = "user_daily_stats"
)
@EntityListeners(AuditingEntityListener.class)
public class UserDailyStatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_routine_id", nullable = false)
    private Long userRoutineId;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "today_routine", nullable = false)
    private List<RouteXYDto> todayRoutine;

    @Column(name = "transport_cost", nullable = false)
    private int transportCost;

    @Column(name = "total_distance_meter", nullable = false)
    private int totalDistanceMeter;

    @Column(name = "estimated_calories", nullable = false)
    private int estimatedCalories;

    @Column(name = "is_late", nullable = false)
    private boolean isLate;

    @Column(name = "is_route_followed", nullable = false)
    private boolean isRouteFollowed;

    @Column(name = "is_comfort", nullable = false)
    private boolean isComfort;

    @Column(name = "sat_wait_time_score")
    private Integer satWaitTimeScore;

    @Column(name = "sat_eta_score")
    private Integer satEtaScore;

    @Column(name = "sat_route_score")
    private Integer satRouteScore;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}