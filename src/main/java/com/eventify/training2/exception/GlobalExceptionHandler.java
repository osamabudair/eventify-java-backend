package com.eventify.training2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  // 404 Not Found
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.NOT_FOUND.value());
    errorDetails.put("error", "Not Found");
    errorDetails.put("message", ex.getMessage());

    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  // 400 Bad Request
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
