package com.ruttu.project_02_backend.repository.stats;

import com.ruttu.project_02_backend.entity.stats.UserDailyStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDailyStatsRepository extends JpaRepository<UserDailyStatsEntity, Long> {
    void deleteByUserIdIn(List<Long> userIds);
}

