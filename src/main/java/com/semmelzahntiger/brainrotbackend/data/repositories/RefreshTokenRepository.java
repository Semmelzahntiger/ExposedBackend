package com.semmelzahntiger.brainrotbackend.data.repositories;

import com.semmelzahntiger.brainrotbackend.data.entities.RefreshTokenEntity;
import com.semmelzahntiger.brainrotbackend.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);
    Optional<RefreshTokenEntity> findByOwnerEntity(UserEntity userId);
    Optional<RefreshTokenEntity> findByOwnerEntity_UserId(UUID userId);
}
