package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OdsayResponseDto {

    private Result result;

    @Getter
    @Setter
    public static class Result {
        private int searchType;
        private int outTrafficCheck;
        private List<OdsayPathDto> path;
    }
}