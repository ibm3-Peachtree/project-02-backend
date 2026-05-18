package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutineResponseDto {

    private Long routeId;
    private String message;
}
