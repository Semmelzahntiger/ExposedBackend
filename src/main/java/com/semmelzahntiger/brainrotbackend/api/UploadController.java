package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.UserPrincipal;
import com.semmelzahntiger.brainrotbackend.service.FileDecoderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/data")
public class UploadController {
    private final FileDecoderService fileDecoderService;

    public UploadController(FileDecoderService fileDecoderService) {
        this.fileDecoderService = fileDecoderService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadUserData(@AuthenticationPrincipal UserPrincipal principal, @RequestParam("type") String type,  @RequestParam("file") MultipartFile file) {
        if(!type.equals("tiktok") && !type.equals("instagram")) {
            return ResponseEntity.badRequest().body("Unsupported file type declared");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUserData(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok().build();
    }
}
