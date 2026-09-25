package com.eventify.training2.controller;

import com.eventify.training2.model.User;
import com.eventify.training2.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
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
}
