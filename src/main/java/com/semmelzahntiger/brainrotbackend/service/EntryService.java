package com.semmelzahntiger.brainrotbackend.service;

import com.semmelzahntiger.brainrotbackend.data.repositories.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.data.repositories.UserRepository;
import com.semmelzahntiger.brainrotbackend.data.entities.DataEntry;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EntryService {
    private final UserRepository userRepository;
    private final DataEntryRepository dataEntryRepository;

    public EntryService(UserRepository userRepository, DataEntryRepository dataEntryRepository) {
        this.userRepository = userRepository;
        this.dataEntryRepository = dataEntryRepository;
    }

    @Transactional
    public boolean updateEntriesOfPlatform(UUID userId, List<SocialMediaResource> socialMediaResources, SocialMediaPlatform platform) {
        String platformName = platform.getName();
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            return false;
        }
        dataEntryRepository.deleteByUserEntity_UserIdAndPlatform(userId, platformName);
        List<DataEntry> entries = socialMediaResources.stream()
                .map(entry -> DataEntry.fromSocialMediaResource(userId, entry)).toList();
        dataEntryRepository.saveAll(entries);
        return true;
    }
    @Transactional
    public boolean deleteEntries(UUID userId) {
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            return false;
        }
        dataEntryRepository.deleteByUserEntity_UserId(userId);
        return true;
    }

}
