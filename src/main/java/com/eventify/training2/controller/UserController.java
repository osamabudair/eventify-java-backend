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

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService){
    this.userService = userService;
  }

  @GetMapping()
  public List<User> getAllUsers(){
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public User getUserById(@PathVariable Long id){
    return userService.getUserById(id);
  }

  @PostMapping()
  public User createUser(@RequestBody User user){
    return userService.createUser(user);
  }

  @PutMapping("/{id}")
  public User updateUser(@PathVariable Long id, @RequestBody User user) {
    return userService.updateUser(id, user);
  }

  @DeleteMapping("/{id}")
  public String deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return "User with id " + id + " has been deleted successfully!";
  }

  @GetMapping("/clubs")
  public ResponseEntity<List<ClubDropdownDTO>> getAllClubs() {
    return ResponseEntity.ok(userService.getAllClubs());
  }

  // get user profile
  @GetMapping("/{userId}/profile")
  public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserProfile(userId));
  }

  // save change
  @PutMapping("/{userId}/profile")
  public ResponseEntity<UserProfileDTO> updateProfile(
          @PathVariable Long userId,
          @Valid @RequestBody UpdateProfileDTO dto) {
    return ResponseEntity.ok(userService.updateProfile(userId, dto));
  }
}
