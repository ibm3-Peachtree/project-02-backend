package com.ruttu.project_02_backend.repository.prod.report;

import com.ruttu.project_02_backend.entity.prod.report.UserMonthlyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMonthlyReportRepository extends JpaRepository<UserMonthlyReportEntity, Long> {
    void deleteByUserIdIn(List<Long> userIds);
}
