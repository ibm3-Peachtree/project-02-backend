package com.ruttu.project_02_backend.repository.auth;

import com.ruttu.project_02_backend.entity.auth.OauthMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthMngtRepository extends JpaRepository<OauthMngtEntity, Long> {
}
