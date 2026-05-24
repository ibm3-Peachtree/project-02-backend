package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BusSectionDto extends TransitSectionDto {

    public BusSectionDto(
            int sectionTime,
            List<String> no,
            String start,
            String end,
            Integer stationCount,
            List<String> stationName
    ) {
        super(sectionTime, no, start, end, stationCount, stationName);
    }

    @Override
    public String getType() {
        return "bus";
    }
}