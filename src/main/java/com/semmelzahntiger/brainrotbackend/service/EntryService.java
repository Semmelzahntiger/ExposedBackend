package com.semmelzahntiger.brainrotbackend.service;

import com.semmelzahntiger.brainrotbackend.data.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.data.entities.DataEntry;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EntryService {
    private final DataEntryRepository dataEntryRepository;

    public EntryService(DataEntryRepository dataEntryRepository) {
        this.dataEntryRepository = dataEntryRepository;
    }

    @Transactional
    public void updateEntries(UUID userId, List<SocialMediaResource> socialMediaResources) {
        dataEntryRepository.deleteByUserid(userId);
        List<DataEntry> entries = socialMediaResources.stream()
                .map(entry -> DataEntry.fromSocialMediaResource(userId, entry)).toList();
        dataEntryRepository.saveAll(entries);
    }

}
