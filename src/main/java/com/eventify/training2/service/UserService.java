package com.eventify.training2.service;

import com.eventify.training2.dto.*;
import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
  @Autowired
  private final UserRepo userRepo;

  // Constructor
  public UserService(UserRepo userRepo){
    this.userRepo = userRepo;
  }

  // Get all users
  public List<User> getAllUsers(){
    return userRepo.findAll();
  }

  // Get a user by id
  public User getUserById(Long id){
    return userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  // Create a user
  public User createUser(User user){
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("User cannot be empty!");
    }
    return userRepo.save(user);
  }

  // Update a user
  public User updateUser(Long id, User newUserData){
    User existingUser = getUserById(id);

    existingUser.setName(newUserData.getName());
    existingUser.setEmail(newUserData.getEmail());

    return userRepo.save(existingUser);
  }

  // Delete an event
  public void deleteUser(Long id) {
    if (!userRepo.existsById(id)) {
      throw new RuntimeException("User not found with id: " + id);
    }
    userRepo.deleteById(id);
  }

  // Register
  public User register(RegisterRequestDTO dto) {

    // check password match
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match!");
    }

    // check email duplicate
    if (userRepo.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email is already registered!");
    }

    User user = new User();
    user.setName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setPassword(dto.getPassword());
    user.setRole(dto.getRole());

    return userRepo.save(user);
  }

  // Login
  public User login(LoginRequestDTO dto) {

    User user = userRepo.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

    // check password match
    if (!user.getPassword().equals(dto.getPassword())) {
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

  // get profile data
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

  // update profile data
  public UserProfileDTO updateProfile(Long userId, UpdateProfileDTO dto) {
    User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    // change name and image
    user.setName(dto.getName());
    if (dto.getProfileImageUrl() != null) {
      user.setProfileImageUrl(dto.getProfileImageUrl());
    }

    // check if user want to change the password
    if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
      // check put current password
      if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
        throw new RuntimeException("Current password is required to set a new password!");
      }

      // check correct current password
      if (!user.getPassword().equals(dto.getCurrentPassword())) {
        throw new RuntimeException("Current password does not match!");
      }

      // check compare new password with new confirmed password
      if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
        throw new RuntimeException("New passwords do not match!");
      }

      // save new password
      user.setPassword(dto.getNewPassword());
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
