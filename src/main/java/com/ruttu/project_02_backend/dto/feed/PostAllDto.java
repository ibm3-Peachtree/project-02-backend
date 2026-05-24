package com.ruttu.project_02_backend.dto.feed;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostAllDto {

    private Long postId;
    private String title;
    private String route;
    private String station;
    private int viewCount;
    private LocalDateTime createdAt;
}
