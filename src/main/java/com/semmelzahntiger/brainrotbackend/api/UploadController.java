package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.UserPrincipal;
import com.semmelzahntiger.brainrotbackend.service.EntryService;
import com.semmelzahntiger.brainrotbackend.service.FileDecoderService;
import com.semmelzahntiger.brainrotbackend.service.InstagramParser;
import com.semmelzahntiger.brainrotbackend.service.SocialMediaPlatform;
import com.semmelzahntiger.brainrotbackend.service.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.service.TikTokParser;
import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/data")
public class UploadController {
    private final FileDecoderService fileDecoderService;
    private final InstagramParser instagramParser;
    private final TikTokParser tikTokParser;
    private final EntryService entryService;

    public UploadController(FileDecoderService fileDecoderService, InstagramParser instagramParser, TikTokParser tikTokParser, EntryService entryService) {
        this.fileDecoderService = fileDecoderService;
        this.instagramParser = instagramParser;
        this.tikTokParser = tikTokParser;
        this.entryService = entryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadUserData(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestParam("type") String type,
                                            @RequestParam("file") MultipartFile file)
            throws IOException, SecurityException, MalformedDataStructureException {
        if(!type.equals("tiktok") && !type.equals("instagram")) {
            return ResponseEntity.badRequest().body("Unsupported file type declared");
        }
        InputStream inputStream = file.getInputStream();
        Map<String, byte[]> data =  fileDecoderService.extract(inputStream);
        List<SocialMediaResource> resources;
        SocialMediaPlatform platform;
        if(type.equals("tiktok")) {
            resources = tikTokParser.parseData(data);
            platform = SocialMediaPlatform.TIKTOK;
        }
        else {
            resources = instagramParser.parseData(data);
            platform = SocialMediaPlatform.INSTAGRAM;
        }
        boolean success = entryService.updateEntriesOfPlatform(principal.userUuid(), resources, platform);
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body("Something went wrong during data processing");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUserData(@AuthenticationPrincipal UserPrincipal principal) {
        boolean success = entryService.deleteEntries(principal.userUuid());
        return success ? ResponseEntity.ok().body("Data deleted.") : ResponseEntity.badRequest().body("Data for User doesn't exist or couldn't be deleted");
    }
}
