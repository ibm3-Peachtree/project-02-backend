package com.ruttu.project_02_backend.dto.briefing;

import lombok.Getter;

import java.util.List;

@Getter
public class CalendarIdListDto {
    private List<CalendarId> items;

    @Getter
    public static class CalendarId{
        private String id;
    }
}
