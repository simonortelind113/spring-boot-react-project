package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class) 
    public ResponseEntity<String> handle(RuntimeException ex) {

        if (ex.getMessage().contains("Only managers")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ex.getMessage());
        }

        if (ex.getMessage().contains("not found")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
