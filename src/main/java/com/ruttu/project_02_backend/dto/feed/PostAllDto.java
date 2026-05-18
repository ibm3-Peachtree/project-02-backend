package com.ruttu.project_02_backend.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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
