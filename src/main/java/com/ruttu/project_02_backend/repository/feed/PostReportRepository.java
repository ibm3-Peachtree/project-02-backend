package com.ruttu.project_02_backend.repository.feed;

import com.ruttu.project_02_backend.entity.feed.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
}
