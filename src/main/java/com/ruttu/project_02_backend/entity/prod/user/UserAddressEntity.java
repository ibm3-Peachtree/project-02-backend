package com.ruttu.project_02_backend.entity.prod.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_address")
@EntityListeners(AuditingEntityListener.class)
public class UserAddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "alias")
    private String alias;

    // address 필드 삭제(db user_address 테이블에서도 address 칼럼 삭제)
    // roadAddress, jibunAddress가 있으니까 address칼럼은 필요 없음(중복됨)

    @Column(name = "road_Address", nullable = false)
    private String roadAddress; //도로명 주소 추가

    @Column(name = "jibun_Address", nullable = false)
    private String jibunAddress; // 지번 주소 추가

    @Column(name = "lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(name = "lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

}