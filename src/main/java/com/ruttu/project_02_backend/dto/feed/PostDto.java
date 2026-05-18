package com.ruttu.project_02_backend.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private String title;
    private String content;
    private Optional<File> image;
    private String route;
    private String station;
    private String issueType;
}
