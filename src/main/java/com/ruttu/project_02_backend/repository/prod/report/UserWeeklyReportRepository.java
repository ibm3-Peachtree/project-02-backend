package com.ruttu.project_02_backend.repository.prod.report;

import com.ruttu.project_02_backend.entity.prod.report.UserWeeklyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWeeklyReportRepository extends JpaRepository<UserWeeklyReportEntity, Long> {
    void deleteByUserIdIn(List<Long> userIds);
}
