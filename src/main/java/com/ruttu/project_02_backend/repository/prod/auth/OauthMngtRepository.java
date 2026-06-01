package com.ruttu.project_02_backend.repository.prod.auth;

import com.ruttu.project_02_backend.entity.prod.auth.OauthMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OauthMngtRepository extends JpaRepository<OauthMngtEntity, Long> {
    void deleteByUserIdIn(List<Long> userIds);
}
