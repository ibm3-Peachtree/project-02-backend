package com.ruttu.project_02_backend.dto.briefing;

import com.google.api.client.util.DateTime;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CalendarItemsDto {
    private String summary;
    private String description;
    private List<CalendarItems> items;

    @Getter
    public static class CalendarItems{
        private String summary;
        private String description;
        private String location;
        private CalendarDateTime start;
        private CalendarDateTime end;
    }
    @Getter
    public static class CalendarDateTime{
        private DateTime dateTime;
    }
}
