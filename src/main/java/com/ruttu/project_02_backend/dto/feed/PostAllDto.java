package com.ruttu.project_02_backend.dto.feed;

import com.ruttu.project_02_backend.entity.prod.feed.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostAllDto {

    private Long postId;
    private String author;
    private String title;
    private String lineNumber;     // 1호선, 147번
    private String stationName;
    private Long viewCount;
    private Instant createdAt;

    public PostAllDto(PostEntity postEntity){
        BeanUtils.copyProperties(postEntity, this);
        this.postId = postEntity.getId();
        this.author = "익명";
    }
}
