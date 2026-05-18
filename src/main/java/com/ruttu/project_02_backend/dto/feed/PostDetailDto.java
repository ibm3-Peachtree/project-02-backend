package com.ruttu.project_02_backend.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailDto {

    private Long postId;
    private String title;
    private String content;
    private String imageUrl;
    private int route;
    private String station;
    private String issueType;
    private int viewCount;
    private LocalDateTime createdAt;
}
