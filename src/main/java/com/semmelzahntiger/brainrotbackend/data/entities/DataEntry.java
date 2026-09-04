package com.semmelzahntiger.brainrotbackend.data.entities;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaResource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "entries")
@Getter
@Setter
public class DataEntry {

    public static DataEntry fromSocialMediaResource(UserEntity userEntity, SocialMediaResource resource) {
        DataEntry entry = new DataEntry();
        entry.userEntity = userEntity;
        entry.dataType = resource.getResourceType().getName();
        entry.platform = resource.getPlatform().getName();
        entry.date = resource.getTimestamp();
        entry.value = resource.getResource();
        return entry;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_entry_seq")
    @SequenceGenerator(name = "data_entry_seq", sequenceName = "data_entry_seq", allocationSize = 100)
    @Column(name = "entry_id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID user_id;

    @Column(name = "platform", nullable = false)
    private String platform;

    @Column(name = "data_type",nullable = false)
    private String dataType;

    @Column(name = "timestamp",nullable = false)
    private LocalDate date;

    @Column(name = "ref", nullable = false)
    private String value;
}
