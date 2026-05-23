package com.ruttu.project_02_backend.dto.routine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalkSectionDto implements RouteSectionDto {

    private int sectionTime;

    @Override
    public String getType() {
        return "walk";
    }
}