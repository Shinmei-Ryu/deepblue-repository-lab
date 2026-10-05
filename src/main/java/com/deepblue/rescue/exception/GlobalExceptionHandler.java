package com.deepblue.rescue.exception;

import com.deepblue.rescue.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
            )
    public ResponseEntity<ErrorResponse>
    handleResourceNotFound(
            ResourceNotFoundException ex) {
        HttpStatus status =
                HttpStatus.NOT_FOUND;
        ErrorResponse error =
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        Map.of()
                );
        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(
            BusinessRuleException.class
    )
    public ResponseEntity<ErrorResponse>
    handleBusinessRule(
            BusinessRuleException ex) {
        HttpStatus status =
                HttpStatus.CONFLICT;
        ErrorResponse error =
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        Map.of()
                );
        return ResponseEntity
                .status(status)
                .body(error);
    }



}
