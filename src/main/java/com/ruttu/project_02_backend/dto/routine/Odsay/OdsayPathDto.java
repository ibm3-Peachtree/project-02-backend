package com.ruttu.project_02_backend.dto.routine.Odsay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OdsayPathDto {

    private int pathType;

    private Info info;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info{
        private int totalDistance;
        private int totalTime;
        private int payment;
        private String firstStartStation;
        private String lastEndStation;
        private int totalStationCount;
    }

    private List<SubPath> subPath;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubPath {

        private int trafficType;
        private int sectionTime;

        private List<Lane> lane;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lane{
        private String busNo;
        private Integer subwayCode;
    }

        private String startName;
        private String endName;
        private String way;
        private Integer stationCount;

        private PassStopList passStopList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassStopList {

        private List<Station> stations;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Station {
        private String stationName;
        private Double x;
        private Double y;
        private String arsID;
//        private String type;
    }
}
