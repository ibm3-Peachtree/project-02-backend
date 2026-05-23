package com.ruttu.project_02_backend.entity.routine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "user_routine")
public class UserRoutineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "routine_name", nullable = false)
    private String routineName;

    @Column(name = "origin_alias")
    private String originAlias;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination_alias")
    private String destinationAlias;

    @Column(name = "destination", nullable = false)
    private String destination;

    @ColumnDefault("62")
    @Column(name = "preferred_dow_mask")
    private Integer preferredDowMask;

    @ColumnDefault("'ALL'")
    @Column(name = "preferred_transport")
    private String preferredTransport;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_route", nullable = false)
    private Map<String, Object> preferredRoute;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_route_xy", nullable = false)
    private List<Map<String, Object>> preferredRouteXy;

    @Column(name = "target_arrival_time", nullable = false)
    private LocalTime targetArrivalTime;

    @Column(name = "reco_departure_time", nullable = false)
    private LocalTime recoDepartureTime;

    @Column(name = "estimated_duration_min", nullable = false)
    private Integer estimatedDurationMin;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;


}