package com.ruttu.project_02_backend.service.report;

import com.ruttu.project_02_backend.dto.report.MonthlyReportDto;
import com.ruttu.project_02_backend.dto.report.WeeklyReportDto;
import com.ruttu.project_02_backend.repository.prod.report.UserMonthlyReportRepository;
import com.ruttu.project_02_backend.repository.prod.report.UserWeeklyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserMonthlyReportRepository monthlyReportRepository;
    private final UserWeeklyReportRepository weeklyReportRepository;

    public List<WeeklyReportDto> getWeeklyReport(Long userId){
        return weeklyReportRepository.findAllByUserId(userId)
                .stream()
                .map(WeeklyReportDto::new)
                .toList();
    }

    public List<MonthlyReportDto> getMonthlyReport(Long userId){
        return monthlyReportRepository.findAllByUserId(userId)
                .stream()
                .map(MonthlyReportDto::new)
                .toList();
    }
}
