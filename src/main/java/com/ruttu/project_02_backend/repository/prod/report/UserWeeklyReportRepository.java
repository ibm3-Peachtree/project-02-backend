package com.ruttu.project_02_backend.repository.prod.report;

import com.ruttu.project_02_backend.entity.prod.report.UserWeeklyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWeeklyReportRepository extends JpaRepository<UserWeeklyReportEntity, Long> {
}
