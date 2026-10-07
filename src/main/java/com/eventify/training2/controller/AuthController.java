package com.eventify.training2.controller;

import com.eventify.training2.config.JwtUtils;
import com.eventify.training2.dto.LoginRequestDTO;
import com.eventify.training2.dto.RegisterRequestDTO;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import com.eventify.training2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final UserService userService;
  private final UserRepo userRepo;

  public AuthController(AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils,
                        UserService userService,
                        UserRepo userRepo) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.userService = userService;
    this.userRepo = userRepo;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO dto) {
    User registeredUser = userService.register(dto);
    String token = jwtUtils.generateToken(registeredUser);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Account registered successfully!");
    response.put("token", token);
    response.put("id", registeredUser.getId());
    response.put("name", registeredUser.getName());
    response.put("username", registeredUser.getName());
    response.put("email", registeredUser.getEmail());
    response.put("role", registeredUser.getRole().name());

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User user = userRepo.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found with email: " + dto.getEmail()));

    String token = jwtUtils.generateToken(user);

    Map<String, Object> response = new HashMap<>();
    response.put("token", token);
    response.put("id", user.getId());
    response.put("name", user.getName());
    response.put("username", user.getName());
    response.put("email", user.getEmail());
    response.put("role", user.getRole().name());

    return ResponseEntity.ok(response);
  }
}