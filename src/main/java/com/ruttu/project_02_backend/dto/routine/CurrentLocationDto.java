package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CurrentLocationDto {
    private String status;
    private Long updatedAt;
}
