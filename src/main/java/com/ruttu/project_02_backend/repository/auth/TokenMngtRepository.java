package com.ruttu.project_02_backend.repository.auth;

import com.ruttu.project_02_backend.entity.auth.TokenMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenMngtRepository extends JpaRepository<TokenMngtEntity, Long> {
    Optional<TokenMngtEntity> findByUserId(Long userId);
}
