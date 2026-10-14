package com.eventify.training2.controller;

import com.eventify.training2.config.JwtUtils;
import com.eventify.training2.dto.LoginRequestDTO;
import com.eventify.training2.dto.RegisterRequestDTO;
import com.eventify.training2.model.Role;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils,
                        UserService userService,
                        UserRepo userRepo,
                        PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.userService = userService;
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Register a new student or club leader account.
   * Normalizes incoming email string to lowercase before delegating to UserService.
   *
   * @param dto Registration payload
   * @return Registered user details with JWT token or pending approval status
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO dto) {
    if (dto.getEmail() != null) {
      dto.setEmail(dto.getEmail().trim().toLowerCase());
    }

    User registeredUser = userService.register(dto);

    Map<String, Object> response = new HashMap<>();
    response.put("id", registeredUser.getId());
    response.put("name", registeredUser.getName());
    response.put("username", registeredUser.getName());
    response.put("email", registeredUser.getEmail());
    response.put("role", registeredUser.getRole().name());

    // If registered user is a Club Leader, require admin approval
    if (registeredUser.getRole() == Role.CLUB_LEADER && !Boolean.TRUE.equals(registeredUser.getApproved())) {
      response.put("pendingApproval", true);
      response.put("message", "Please wait until you are approved by the admin");
      response.put("detailedMessage", "Please wait until your registration is approved and verified");
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    String token = jwtUtils.generateToken(registeredUser);
    response.put("message", "Account registered successfully!");
    response.put("token", token);
    response.put("pendingApproval", false);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Authenticate user credentials with case-insensitive email lookup.
   *
   * @param dto Login credentials
   * @return Authenticated user profile and signed JWT token
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
    final String normalizedEmail = dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : "";

    User user = userRepo.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

    // Check if student account is suspended before authentication
    if (user.getRole() == Role.STUDENT && Boolean.FALSE.equals(user.getApproved())) {
      if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "ACCOUNT_SUSPENDED");
        response.put("suspended", true);
        response.put("role", "STUDENT");
        response.put("message", "Account Currently Suspended");
        response.put("detailedMessage", "Your account has been suspended by the system administrator.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }
    }

    Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(normalizedEmail, dto.getPassword())
      );
    } catch (org.springframework.security.authentication.DisabledException |
             org.springframework.security.authentication.LockedException e) {
      if (user.getRole() == Role.STUDENT && Boolean.FALSE.equals(user.getApproved())) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "ACCOUNT_SUSPENDED");
        response.put("suspended", true);
        response.put("role", "STUDENT");
        response.put("message", "Account Currently Suspended");
        response.put("detailedMessage", "Your account has been suspended by the system administrator.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }
      throw e;
    }

    // Check if leader is pending admin approval
    if (user.getRole() == Role.CLUB_LEADER && !Boolean.TRUE.equals(user.getApproved())) {
      Map<String, Object> response = new HashMap<>();
      response.put("error", "PENDING_APPROVAL");
      response.put("pendingApproval", true);
      response.put("role", "CLUB_LEADER");
      response.put("message", "Please wait until your registration is approved and verified");
      response.put("detailedMessage", "Please wait until you are approved by the admin");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Auto-upgrade stored password to high-security JWT token if not already in JWT format
    if (passwordEncoder.upgradeEncoding(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(dto.getPassword()));
      userRepo.save(user);
    }

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