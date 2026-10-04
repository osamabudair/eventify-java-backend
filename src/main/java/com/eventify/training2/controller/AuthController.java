package com.eventify.training2.controller;

import com.eventify.training2.dto.LoginRequestDTO;
import com.eventify.training2.dto.RegisterRequestDTO;
import com.eventify.training2.model.User;
import com.eventify.training2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO dto){
    User registeredUser = userService.register(dto);
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Account registered successfully!");
    response.put("id", registeredUser.getId());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto){
    User user = userService.login(dto);

    Map<String, Object> response = new HashMap<>();
    response.put("id", user.getId());
    response.put("name", user.getName());
    response.put("username", user.getName());
    response.put("email", user.getEmail());
    response.put("role", user.getRole().name());
    response.put("token", "mock-token-for-dev");

    return ResponseEntity.ok(response);
  }
}