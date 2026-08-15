package com.semmelzahntiger.brainrotbackend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUserId(UUID userId);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByUsername(String username);
    void deleteByUserId(UUID uuid);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserId(UUID uuid);
}
