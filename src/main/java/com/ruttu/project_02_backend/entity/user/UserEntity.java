package com.ruttu.project_02_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String email; //이메일
    private String provider; //어떤 계정으로 로그인 했나
    @Column(unique = true)
    private String providerId; //Google이 사용자에게 부여하는 "고유 ID"

    @Column(name = "nickname")
    private String nickname;

    @ColumnDefault("'USER'")
    @Column(name = "role")
    private String role;

    @ColumnDefault("'ACTIVE'")
    @Column(name = "status")
    private String status;

    @ColumnDefault("1")
    @Column(name = "is_reco_departure_noti_enabled")
    private Boolean isRecoDepartureNotiEnabled;

    @Column(name = "brief_notification_time")
    private LocalTime briefNotificationTime;

    @ColumnDefault("0")
    @Column(name = "brief_notification_day_offset")
    private Integer briefNotificationDayOffset;

    @ColumnDefault("1")
    @Column(name = "is_brief_notification_enabled")
    private Boolean isBriefNotificationEnabled;

    @ColumnDefault("5")
    @Column(name = "transit_notification_min")
    private Integer transitNotificationMin;

    @ColumnDefault("1")
    @Column(name = "allowed_tts")
    private Boolean allowedTts;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;


}