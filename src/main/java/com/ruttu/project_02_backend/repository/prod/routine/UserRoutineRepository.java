package com.ruttu.project_02_backend.repository.prod.routine;

import com.ruttu.project_02_backend.entity.prod.routine.UserRoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface UserRoutineRepository extends JpaRepository<UserRoutineEntity, Long> {

    List<UserRoutineEntity> findAllByTargetArrivalTimeAndUserId(
            LocalTime targetArrivalTime,
            Long userId
    );

    List<UserRoutineEntity>  findAllByUserId(Long userId);
    boolean existsByUserIdAndOriginAlias(Long userId, String originAlias);
    boolean existsByUserIdAndDestinationAlias(Long userId, String destinationAlias);

    void deleteByUserIdIn(List<Long> userIds);
}

