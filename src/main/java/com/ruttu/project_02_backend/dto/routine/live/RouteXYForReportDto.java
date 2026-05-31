package com.ruttu.project_02_backend.dto.routine.live;


import com.ruttu.project_02_backend.dto.routine.odsay.RouteXYDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteXYForReportDto {
    private Long routineId;
    private List<RouteXYDto> routeXYDtoList;
}