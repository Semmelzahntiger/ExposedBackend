package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.json.response.UnauthorizedResponse;
import com.semmelzahntiger.brainrotbackend.util.exceptions.DeclaredDataFormatException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice(basePackageClasses = {UploadController.class})
public class ApiControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<UnauthorizedResponse> handleUnauthorizedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(new UnauthorizedResponse());
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
    }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }
    @ExceptionHandler(DeclaredDataFormatException.class)
    public ResponseEntity<?> handleDeclaredDataFormatException(DeclaredDataFormatException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).build();
    }
}
