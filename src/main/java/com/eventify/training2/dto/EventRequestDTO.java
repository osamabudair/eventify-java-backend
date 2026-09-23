package com.eventify.training2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EventRequestDTO {
  @NotBlank(message = "Title is mandatory")
  @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
  private String title;

  private String description;

  @NotBlank(message = "Location is mandatory")
  private String location;

  @NotNull(message = "Organizer ID is mandatory")
  private Long organizerId;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Long getOrganizerId() {
    return organizerId;
  }

  public void setOrganizerId(Long organizerId) {
    this.organizerId = organizerId;
  }
}
