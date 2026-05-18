package com.ruttu.project_02_backend.repository.auth;

import com.ruttu.project_02_backend.entity.auth.TokenMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenMngtRepository extends JpaRepository<TokenMngtEntity, Long> {
}
