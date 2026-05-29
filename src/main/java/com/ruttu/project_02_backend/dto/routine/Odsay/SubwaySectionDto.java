package com.ruttu.project_02_backend.dto.routine.Odsay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class SubwaySectionDto extends TransitSectionDto {

    @Setter
    private String way;

    public SubwaySectionDto(
            int sectionTime,
            List<String> no,
            String start,
            String end,
            Integer stationCount,
            List<String> stationName,
            String way
    ) {
        super(sectionTime, no, start, end, stationCount, stationName);
        this.way = way;
    }

    @Override
    public String getType() {
        return "subway";
    }
}
