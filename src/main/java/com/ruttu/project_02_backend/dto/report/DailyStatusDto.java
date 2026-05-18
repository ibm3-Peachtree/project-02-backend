package com.ruttu.project_02_backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyStatusDto {
    private int commuteTimeMin;
    private boolean isComfort;
}
