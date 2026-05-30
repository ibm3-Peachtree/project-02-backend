package com.ruttu.project_02_backend.entity.prod.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "token_mngt")
public class TokenMngtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Column(name = "replaced_by_token_id")
    private Integer replacedByTokenId;

    @Column(name = "device_info")
    private String deviceInfo;

    @ColumnDefault("0")
    @Column(name = "revoked")
    private Boolean revoked;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}