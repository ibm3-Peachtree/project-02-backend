package com.ruttu.project_02_backend.dto.feed;

import java.io.File;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
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
