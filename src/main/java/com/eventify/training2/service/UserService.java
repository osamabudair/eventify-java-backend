package com.eventify.training2.service;

import com.eventify.training2.dto.*;
import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder){
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  public List<User> getAllUsers(){
    return userRepo.findAll();
  }

  public User getUserById(Long id){
    return userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  public User createUser(User user){
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("User cannot be empty!");
    }
    if (user.getPassword() != null && !user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepo.save(user);
  }

  public User updateUser(Long id, User newUserData){
    User existingUser = getUserById(id);
    existingUser.setName(newUserData.getName());
    existingUser.setEmail(newUserData.getEmail());
    return userRepo.save(existingUser);
  }

  public void deleteUser(Long id) {
    if (!userRepo.existsById(id)) {
      throw new RuntimeException("User not found with id: " + id);
    }
    userRepo.deleteById(id);
  }

  public User register(RegisterRequestDTO dto) {
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match!");
    }

    if (userRepo.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email is already registered!");
    }

    User user = new User();
    user.setName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRole(dto.getRole());

    return userRepo.save(user);
  }

  public User login(LoginRequestDTO dto) {
    User user = userRepo.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid email or password!");
    }

    return user;
  }

  public List<ClubDropdownDTO> getAllClubs() {
    return userRepo.findByRole(Role.CLUB_LEADER)
            .stream()
            .map(leader -> new ClubDropdownDTO(leader.getId(), leader.getName()))
            .collect(Collectors.toList());
  }

  public UserProfileDTO getUserProfile(Long userId) {
    User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    return new UserProfileDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getProfileImageUrl()
    );
  }

  public UserProfileDTO updateProfile(Long userId, UpdateProfileDTO dto) {
    User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    user.setName(dto.getName());
    if (dto.getProfileImageUrl() != null) {
      user.setProfileImageUrl(dto.getProfileImageUrl());
    }

    if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
      if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
        throw new RuntimeException("Current password is required to set a new password!");
      }

      boolean currentMatches = passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword());
      if (!currentMatches) {
        throw new RuntimeException("Current password does not match!");
      }

      if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
        throw new RuntimeException("New passwords do not match!");
      }

      user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    userRepo.save(user);

    return new UserProfileDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getProfileImageUrl()
    );
  }
}