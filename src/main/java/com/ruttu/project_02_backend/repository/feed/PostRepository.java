package com.ruttu.project_02_backend.repository.feed;

import com.ruttu.project_02_backend.entity.feed.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
