package com.ruttu.project_02_backend.repository.prod.user;

import com.ruttu.project_02_backend.entity.prod.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findAllByDepartureMinutes(String departureMinutes);
    List<UserEntity> findAllByMorningTime(LocalTime morningTime);


    boolean existsByNickname(String nickname);

    @Query("""
        SELECT u.id FROM UserEntity u
        WHERE u.status = 'WITHDRAWN'
        AND u.withdrawnAt < :limit
    """)
    List<Long> findExpiredUserIds(@Param("limit") Instant limit);

}
