package org.example.carrent.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request: " + e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Conflict: " + e.getMessage()));
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> internalServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error: " + e.getMessage()));
    }
}