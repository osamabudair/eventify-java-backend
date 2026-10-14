package com.eventify.training2.service;

import com.eventify.training2.dto.*;
import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing user profiles, authentication registration, credentials verification,
 * and club organizer listings. All email addresses are strictly normalized (.trim().toLowerCase())
 * to ensure robust case-insensitive handling.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Retrieve all registered users.
   */
  public List<User> getAllUsers() {
    return userRepo.findAll();
  }

  /**
   * Retrieve a user by their primary key ID.
   */
  public User getUserById(Long id) {
    return userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  /**
   * Create and persist a new user entity with normalized email.
   */
  @Transactional
  public User createUser(User user) {
    if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("User email cannot be empty!");
    }
    user.setEmail(user.getEmail().trim().toLowerCase());

    if (user.getPassword() != null && !user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepo.save(user);
  }

  /**
   * Update basic profile information of an existing user.
   */
  @Transactional
  public User updateUser(Long id, User newUserData) {
    User existingUser = getUserById(id);
    if (newUserData.getName() != null) {
      existingUser.setName(newUserData.getName().trim());
    }
    if (newUserData.getEmail() != null && !newUserData.getEmail().trim().isEmpty()) {
      existingUser.setEmail(newUserData.getEmail().trim().toLowerCase());
    }
    return userRepo.save(existingUser);
  }

  /**
   * Delete a user entity by ID.
   */
  @Transactional
  public void deleteUser(Long id) {
    if (!userRepo.existsById(id)) {
      throw new RuntimeException("User not found with id: " + id);
    }
    userRepo.deleteById(id);
  }

  /**
   * Register a new user with case-insensitive email validation and hashing.
   * Club leaders are placed in pending state requiring admin approval.
   */
  @Transactional
  public User register(RegisterRequestDTO dto) {
    if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email is required!");
    }

    // Normalize incoming email string to lowercase
    final String normalizedEmail = dto.getEmail().trim().toLowerCase();

    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match!");
    }

    if (userRepo.existsByEmailIgnoreCase(normalizedEmail)) {
      throw new IllegalArgumentException("Email is already registered!");
    }

    User user = new User();
    user.setName(dto.getFullName() != null ? dto.getFullName().trim() : "");
    user.setEmail(normalizedEmail);
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRole(dto.getRole());
    user.setApproved(dto.getRole() != Role.CLUB_LEADER);
    user.setCreatedAt(LocalDateTime.now());

    return userRepo.save(user);
  }

  /**
   * Authenticate user credentials using case-insensitive email lookup.
   */
  public User login(LoginRequestDTO dto) {
    if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
      throw new RuntimeException("Invalid email or password!");
    }

    final String normalizedEmail = dto.getEmail().trim().toLowerCase();

    User user = userRepo.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid email or password!");
    }

    if (user.getRole() == Role.STUDENT && Boolean.FALSE.equals(user.getApproved())) {
      throw new RuntimeException("Account currently suspended.");
    }

    if (user.getRole() == Role.CLUB_LEADER && !Boolean.TRUE.equals(user.getApproved())) {
      throw new RuntimeException("Please wait until your registration is approved and verified.");
    }

    return user;
  }

  /**
   * Retrieve list of registered clubs for dropdown selectors.
   */
  public List<ClubDropdownDTO> getAllClubs() {
    return userRepo.findByRole(Role.CLUB_LEADER)
            .stream()
            .map(leader -> new ClubDropdownDTO(leader.getId(), leader.getName()))
            .collect(Collectors.toList());
  }

  /**
   * Retrieve structured profile information for a user.
   */
  public UserProfileDTO getUserProfile(Long userId) {
    User user = getUserById(userId);

    return new UserProfileDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getProfileImageUrl()
    );
  }

  /**
   * Update student or leader profile details and password securely.
   */
  @Transactional
  public UserProfileDTO updateProfile(Long userId, UpdateProfileDTO dto) {
    User user = getUserById(userId);

    if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
      user.setName(dto.getName().trim());
    }
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