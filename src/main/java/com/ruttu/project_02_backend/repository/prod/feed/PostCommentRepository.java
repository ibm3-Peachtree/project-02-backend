package com.ruttu.project_02_backend.repository.prod.feed;

import com.ruttu.project_02_backend.entity.prod.feed.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
    void deleteByUserIdIn(List<Long> userIds);
}
