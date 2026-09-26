package com.eventify.training2.dto;

import com.eventify.training2.model.Role;

public class UserProfileDTO {
  private Long id;
  private String name;
  private String email;
  private Role role;
  private String profileImageUrl;

  public UserProfileDTO(Long id, String name, String email, Role role, String profileImageUrl) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.profileImageUrl = profileImageUrl;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public Role getRole() { return role; }
  public String getProfileImageUrl() { return profileImageUrl; }
}
