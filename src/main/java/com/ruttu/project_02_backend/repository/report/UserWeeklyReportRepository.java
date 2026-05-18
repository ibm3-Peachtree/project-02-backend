package com.ruttu.project_02_backend.repository.report;

import com.ruttu.project_02_backend.entity.report.UserWeeklyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWeeklyReportRepository extends JpaRepository<UserWeeklyReportEntity, Long> {
}
