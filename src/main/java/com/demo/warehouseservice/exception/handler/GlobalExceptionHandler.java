package com.demo.warehouseservice.exception.handler;

import com.demo.warehouseservice.dto.ErrorDetails;
import com.demo.warehouseservice.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle exception
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDetails> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .message("Exception")
                        .details(exception.getMessage())
                        .build());
    }

    /**
     * Handle validation exception
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(value = {ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDetails> handleValidationException(Exception exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .message("ValidationException")
                        .details(exception.getMessage())
                        .build());
    }

}
