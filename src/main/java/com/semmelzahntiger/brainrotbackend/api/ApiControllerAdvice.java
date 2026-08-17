package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.json.UnauthorizedResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice(basePackageClasses = {UploadController.class})
public class ApiControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<UnauthorizedResponse> handleUnauthorizedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(new UnauthorizedResponse());
    }
}
