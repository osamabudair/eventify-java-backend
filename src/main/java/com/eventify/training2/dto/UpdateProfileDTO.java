package com.eventify.training2.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileDTO {
  @NotBlank(message = "Name cannot be empty")
  private String name;

  private String profileImageUrl;

  private String currentPassword;
  private String newPassword;
  private String confirmNewPassword;

  // Getters and Setters
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getProfileImageUrl() { return profileImageUrl; }
  public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

  public String getCurrentPassword() { return currentPassword; }
  public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

  public String getNewPassword() { return newPassword; }
  public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

  public String getConfirmNewPassword() { return confirmNewPassword; }
  public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
}
