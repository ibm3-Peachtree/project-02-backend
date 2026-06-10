package com.ruttu.project_02_backend.dto.feed;

import com.ruttu.project_02_backend.entity.prod.feed.PostEntity;
import com.ruttu.project_02_backend.entity.prod.feed.enumtype.IssueType;
import com.ruttu.project_02_backend.entity.prod.feed.enumtype.TransportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailDto {

    private Long postId;
    private String title;
    private String content;
    private String image;
    private TransportType transportType;
    private String lineNumber;
    private String stationName;
    private IssueType issueType;
    private Long viewCount;
    private Instant createdAt;

    public PostDetailDto(PostEntity postEntity){
        BeanUtils.copyProperties(postEntity, this);
        this.postId = postEntity.getId();
        }
}
