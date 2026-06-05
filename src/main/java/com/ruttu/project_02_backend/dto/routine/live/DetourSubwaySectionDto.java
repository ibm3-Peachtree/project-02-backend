package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.odsay.RouteSectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetourSubwaySectionDto implements RouteSectionDto {

    private int sectionTime;
    private List<String> no;
    private String start;
    private String end;
    private Integer stationCount;
    private List<String> stationName;
    private String way;

    @Override
    public String getType() {
        return "subway";
    }

    public DetourSubwaySectionDto(DetourDto.pathSegments p) {
        this.sectionTime = (int) p.getSegment_duration_min();
        this.no = p.getDisplay_name();

        this.start = p.getStations().getFirst().getName();
        this.end = p.getStations().getLast().getName();

        this.stationCount = p.getStop_count() + 1;

        this.stationName = p.getStations()
                .stream()
                .limit(Math.max(0, p.getStations().size() - 1))
                .map(s -> s.getName().split(" ")[0])
                .toList();

        this.way = p.getStations().size() > 1
                ? p.getStations().get(1).getName().split(" ")[0]
                : null;
    }
}
