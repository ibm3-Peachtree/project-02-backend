package com.ruttu.project_02_backend.repository.report;

import com.ruttu.project_02_backend.entity.report.UserMonthlyReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMonthlyReportRepository extends JpaRepository<UserMonthlyReportEntity, Long> {
}
