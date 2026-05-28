package com.ruttu.project_02_backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyStatusDto {
    private int commuteTimeMin;
    private boolean isComfort;
}
