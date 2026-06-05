package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.odsay.RouteSectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetourWalkSectionDto implements RouteSectionDto {

    private int sectionTime;
    private List<String> no;

    @Override
    public String getType() {
        return "walk";
    }

    public DetourWalkSectionDto(DetourDto.pathSegments p) {
        this.sectionTime = (int) p.getSegment_duration_min();
        this.no = null;
    }
}
