package com.eventify.training2.controller;

import com.eventify.training2.dto.LoginRequestDTO;
import com.eventify.training2.dto.RegisterRequestDTO;
import com.eventify.training2.model.User;
import com.eventify.training2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  @Autowired
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<User> register(@Valid @RequestBody RegisterRequestDTO dto){
    return new ResponseEntity<>(userService.register(dto), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<User> login(@Valid @RequestBody LoginRequestDTO dto){
    return ResponseEntity.ok(userService.login(dto));
  }
}
