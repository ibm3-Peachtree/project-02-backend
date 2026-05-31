package com.ruttu.project_02_backend.dto.routine.Odsay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalkSectionDto implements RouteSectionDto {

    private int sectionTime;
    private List<String> no;

    @Override
    public String getType() {
        return "walk";
    }
}