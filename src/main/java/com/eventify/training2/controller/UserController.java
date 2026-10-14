package com.eventify.training2.controller;

import com.eventify.training2.dto.ClubDropdownDTO;
import com.eventify.training2.dto.UpdateProfileDTO;
import com.eventify.training2.dto.UserProfileDTO;
import com.eventify.training2.model.User;
import com.eventify.training2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller managing user records, club dropdown lists, and user profile management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Retrieve all registered users.
   */
  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  /**
   * Retrieve a single user by ID.
   */
  @GetMapping("/{id}")
  public User getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  /**
   * Create a new user record.
   */
  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.createUser(user);
  }

  /**
   * Update an existing user's data.
   */
  @PutMapping("/{id}")
  public User updateUser(@PathVariable Long id, @RequestBody User user) {
    return userService.updateUser(id, user);
  }

  /**
   * Delete a user record by ID.
   */
  @DeleteMapping("/{id}")
  public String deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return "User with id " + id + " has been deleted successfully!";
  }

  /**
   * Retrieve list of active club leaders for selection dropdowns.
   */
  @GetMapping("/clubs")
  public ResponseEntity<List<ClubDropdownDTO>> getAllClubs() {
    return ResponseEntity.ok(userService.getAllClubs());
  }

  /**
   * Retrieve profile details for the specified user ID.
   */
  @GetMapping("/{userId}/profile")
  public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserProfile(userId));
  }

  /**
   * Update profile details and password for the specified user ID.
   */
  @PutMapping("/{userId}/profile")
  public ResponseEntity<UserProfileDTO> updateProfile(
          @PathVariable Long userId,
          @Valid @RequestBody UpdateProfileDTO dto) {
    return ResponseEntity.ok(userService.updateProfile(userId, dto));
  }
}
