package com.ruttu.project_02_backend.repository.prod.user;

import com.ruttu.project_02_backend.entity.prod.user.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {
    UserAddressEntity findByUserIdAndAlias(Long userId, String alias);

    List<UserAddressEntity> findByUserId(Long userId);

    Optional<UserAddressEntity> findByIdAndUserId(
            Long addressId,
            Long userId
    );

    List<UserAddressEntity> findByUserIdAndAliasContaining(Long userId, String alias);
}
