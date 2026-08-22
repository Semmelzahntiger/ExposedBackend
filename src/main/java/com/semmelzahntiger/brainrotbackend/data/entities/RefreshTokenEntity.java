package com.semmelzahntiger.brainrotbackend.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshTokenEntity {

    public static RefreshTokenEntity getNewRefreshTokenEntity(UserEntity user, String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.ownerEntity = user;
        refreshTokenEntity.token = refreshToken;
        refreshTokenEntity.expiration_date = LocalDate.now().plusDays(3);
        return refreshTokenEntity;
    }

    @Id
    @GeneratedValue
    @Column(name = "token_id", nullable = false)
    private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private UserEntity ownerEntity;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expiration_date;
    @Column(name = "revoke_date")
    private LocalDate revoke_date;
}
