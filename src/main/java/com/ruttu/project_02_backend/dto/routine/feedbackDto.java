package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class feedbackDto {
    private int satWaitTimeScrore;
    private int satEtaScore;
    private int satRouteScore;
}
