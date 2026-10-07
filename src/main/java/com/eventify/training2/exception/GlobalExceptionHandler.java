package com.eventify.training2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 401 Unauthorized for bad credentials
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
    errorDetails.put("error", "Unauthorized");
    errorDetails.put("message", "Invalid email or password!");

    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  // 401 Unauthorized for username not found
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
    errorDetails.put("error", "Unauthorized");
    errorDetails.put("message", "Invalid email or password!");

    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  // 403 Forbidden
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.FORBIDDEN.value());
    errorDetails.put("error", "Forbidden");
    errorDetails.put("message", ex.getMessage());

    return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
  }

  // 400 Bad Request
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
    errorDetails.put("error", "Bad Request");
    errorDetails.put("message", ex.getMessage());

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  // 400 Bad Request for Validation
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // RuntimeException fallback
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
    errorDetails.put("error", "Bad Request");
    errorDetails.put("message", ex.getMessage());

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
}
