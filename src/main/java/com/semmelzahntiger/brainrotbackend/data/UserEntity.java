package com.semmelzahntiger.brainrotbackend.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    @Column(name = "userid")
    private UUID userId;
    private String email;
    private String username;
    private String password;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "authorities", columnDefinition = "text[]")
    private List<String> authorities = new ArrayList<>();


}
