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
public class CommentAllDto {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
}
