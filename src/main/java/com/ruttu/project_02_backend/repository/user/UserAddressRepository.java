package com.ruttu.project_02_backend.repository.user;

import com.ruttu.project_02_backend.entity.user.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
}
