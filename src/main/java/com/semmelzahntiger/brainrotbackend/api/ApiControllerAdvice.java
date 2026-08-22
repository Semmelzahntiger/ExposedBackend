package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.json.response.UnauthorizedResponse;
import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice(basePackageClasses = {UploadController.class})
public class ApiControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Unauthorized");
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
    }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("Request violated security guardrails");
    }
    @ExceptionHandler(MalformedDataStructureException.class)
    public ResponseEntity<Object> handleMalformedDataStructureException(MalformedDataStructureException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_UNPROCESSABLE_CONTENT).body(ex.getMessage());
    }
    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<Object> handleFileSizeLimitExceededException(FileSizeLimitExceededException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE).body(ex.getMessage());
    }
    @ExceptionHandler(FileCountLimitExceededException.class)
    public ResponseEntity<Object> handleFileCountLimitExceededException(FileCountLimitExceededException ex) {
        return ResponseEntity.status(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE).body(ex.getMessage());
    }
}
