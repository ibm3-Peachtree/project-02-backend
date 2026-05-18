package com.ruttu.project_02_backend.repository.auth;

import com.ruttu.project_02_backend.entity.auth.OauthMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthMngtRepository extends JpaRepository<OauthMngtEntity, Long> {
}
