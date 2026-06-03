package com.ruttu.project_02_backend.dto.briefing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGeminiDto {

    private List<Content> candidates;

    @Getter
    public static class Content{
        private Parts content;
    }

    @Getter
    public static class Parts{
        private List<Text> parts;
    }
    @Getter
    public static class Text{
        private String text;
    }
}
