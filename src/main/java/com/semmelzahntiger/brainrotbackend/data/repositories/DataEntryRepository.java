package com.semmelzahntiger.brainrotbackend.data.repositories;

import com.semmelzahntiger.brainrotbackend.data.entities.DataEntry;
import com.semmelzahntiger.brainrotbackend.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DataEntryRepository extends JpaRepository<DataEntry, Long> {

    void deleteByUserEntity(UserEntity user);
    void deleteByUserEntity_UserId(UUID userId);
    void deleteByUserEntityAndPlatform(UserEntity user_id, String platform);
    void deleteByUserEntity_UserIdAndPlatform(UUID user_id, String platform);

    void deleteById(Long id);

    @Query(value = "SELECT * FROM entries WHERE user_id = :userId ORDER BY RANDOM() LIMIT :count",
            nativeQuery = true)
    List<DataEntry> getRandomEntriesByUserid(@Param("userId") UUID userId, @Param("count") Integer count);

    @Query(value = "SELECT * FROM entries " +
            "WHERE user_id IN (:userIds) " +
            "AND (:dataTypes IS NULL OR data_type IN (:dataTypes)) " +
            "AND (:platforms IS NULL OR platform IN (:platforms)) " +
            "AND (:beforeDate IS NULL OR timestamp < CAST(:beforeDate as DATE)) " +
            "ORDER BY RANDOM() " +
            "LIMIT :count",
            nativeQuery = true)
    List<DataEntry> getRandomWithFilters(@Param("userIds") List<UUID> userIds,
                                      @Param("dataTypes") List<String> dataTypes,
                                      @Param("platforms") List<String> platforms,
                                      @Param("beforeDate") LocalDate beforeDate,
                                      @Param("count") int count);

}
