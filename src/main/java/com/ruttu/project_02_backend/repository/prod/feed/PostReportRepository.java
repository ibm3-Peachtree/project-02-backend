package com.ruttu.project_02_backend.repository.prod.feed;

import com.ruttu.project_02_backend.entity.prod.feed.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
}
