package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class feedbackDto {
    private int satWaitTimeScrore;
    private int satEtaScore;
    private int satRouteScore;
}
