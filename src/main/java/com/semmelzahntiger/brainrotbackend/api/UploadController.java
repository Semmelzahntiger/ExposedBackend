package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.UserPrincipal;
import com.semmelzahntiger.brainrotbackend.service.data.EntryService;
import com.semmelzahntiger.brainrotbackend.service.data.FileDecoderService;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaParser;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final Map<String, SocialMediaParser> mediaParsers;
    private final EntryService entryService;

    public UploadController(FileDecoderService fileDecoderService, Map<String, SocialMediaParser> parsers, EntryService entryService) {
        this.fileDecoderService = fileDecoderService;
        this.mediaParsers = parsers;
        this.entryService = entryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> uploadUserData(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestParam("declaredFileType") String declaredFileType,
                                            @RequestParam("file") MultipartFile file)
            throws IOException, SecurityException, MalformedDataStructureException {
        SocialMediaParser parser = mediaParsers.get(declaredFileType);
        if(parser == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Unsupported file type declared"));
        }
        InputStream inputStream = file.getInputStream();
        Map<String, byte[]> data =  fileDecoderService.extract(inputStream);
        List<SocialMediaResource> resources = parser.parseData(data);
        SocialMediaPlatform platform = parser.getPlatform();
        boolean success = entryService.updateEntriesOfPlatform(principal.userUuid(), resources, platform);
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(Map.of("error","Something went wrong during data processing"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String,String>> deleteUserData(@AuthenticationPrincipal UserPrincipal principal) {
        boolean success = entryService.deleteEntries(principal.userUuid());
        return success ? ResponseEntity.ok().body(Map.of("message","Data deleted.")) : ResponseEntity.badRequest().body(Map.of("message","Data for User doesn't exist or couldn't be deleted"));
    }
}
