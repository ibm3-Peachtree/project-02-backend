package com.ruttu.project_02_backend.repository.user;

import com.ruttu.project_02_backend.entity.user.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
}
