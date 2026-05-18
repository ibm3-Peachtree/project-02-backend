package com.ruttu.project_02_backend.repository.feed;

import com.ruttu.project_02_backend.entity.feed.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
}
