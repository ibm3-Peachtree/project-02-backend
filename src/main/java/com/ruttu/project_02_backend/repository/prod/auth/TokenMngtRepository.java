package com.ruttu.project_02_backend.repository.prod.auth;

import com.ruttu.project_02_backend.entity.prod.auth.TokenMngtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenMngtRepository extends JpaRepository<TokenMngtEntity, Long> {
    Optional<TokenMngtEntity> findByRefreshTokenHash(String hashed);

}
