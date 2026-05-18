package com.ruttu.project_02_backend.repository.user;

import com.ruttu.project_02_backend.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
