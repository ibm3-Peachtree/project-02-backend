package com.ruttu.project_02_backend.repository.prod.user;

import com.ruttu.project_02_backend.entity.prod.user.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
    UserAddressEntity findByUserIdAndAlias(Long userId, String alias);
}
