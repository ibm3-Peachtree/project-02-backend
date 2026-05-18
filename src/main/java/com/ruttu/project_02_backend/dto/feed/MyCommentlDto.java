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
public class MyCommentlDto {

    private Long commentId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}
