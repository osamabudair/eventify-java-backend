package com.eventify.training2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class LeaderApprovalDTO {
  private Long id;
  private String name;
  private String email;
  private String role;
  private Boolean approved;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;
  private String profileImageUrl;

  public LeaderApprovalDTO() {
  }

  public LeaderApprovalDTO(Long id, String name, String email, String role, Boolean approved, LocalDateTime createdAt, String profileImageUrl) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.approved = approved;
    this.createdAt = createdAt;
    this.profileImageUrl = profileImageUrl;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Boolean getApproved() {
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }
}

