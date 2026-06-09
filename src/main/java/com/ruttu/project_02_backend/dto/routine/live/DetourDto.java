package com.ruttu.project_02_backend.dto.routine.live;

import com.ruttu.project_02_backend.dto.routine.odsay.RouteDto;
import com.ruttu.project_02_backend.dto.routine.odsay.RouteSectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetourDto {
    private int path_id;
    private double total_duration_min;
    private int transfer_count;
    private int cost;
    private List<pathSegments> path_segments;

    @Getter
    public static class pathSegments{
        private String type;
        private List<String> display_name;
        private double segment_duration_min;
        private int total_distance_m;
        private int stop_count;
        private List<Stations> stations;
    }

    @Getter
    public static class Stations{
        private String name;
        private double x;
        private double y;
        private String ars_id;
    }

}
