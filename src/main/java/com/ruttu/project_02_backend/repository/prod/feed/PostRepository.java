package com.ruttu.project_02_backend.repository.prod.feed;

import com.ruttu.project_02_backend.entity.prod.feed.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    void deleteByUserIdIn(List<Long> userIds);

    List<PostEntity> findAllByOrderByCreatedAtDesc();
}
