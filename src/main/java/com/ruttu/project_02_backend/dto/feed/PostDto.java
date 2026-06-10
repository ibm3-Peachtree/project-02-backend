package com.ruttu.project_02_backend.dto.feed;

import com.ruttu.project_02_backend.entity.prod.feed.enumtype.IssueType;
import com.ruttu.project_02_backend.entity.prod.feed.enumtype.TransportType;
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
    private TransportType transportType; // BUS / SUBWAY
    private String lineNumber;
    private String stationName;
    private IssueType issueType;
}
