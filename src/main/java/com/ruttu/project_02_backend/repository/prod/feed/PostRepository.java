package com.ruttu.project_02_backend.repository.prod.feed;

import com.ruttu.project_02_backend.entity.prod.feed.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
