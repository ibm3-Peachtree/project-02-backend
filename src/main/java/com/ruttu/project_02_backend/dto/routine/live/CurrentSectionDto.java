package com.ruttu.project_02_backend.dto.routine.live;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CurrentSectionDto {
    private int idx; // 현재 어디에 있는지
    private List<String> section;
}
