package com.ruttu.project_02_backend.repository.prod.auth;

import com.ruttu.project_02_backend.entity.prod.auth.OauthMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthMngtRepository extends JpaRepository<OauthMngtEntity, Long> {
}
