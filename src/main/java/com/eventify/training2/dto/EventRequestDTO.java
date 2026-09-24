package com.eventify.training2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventRequestDTO {
  @NotBlank(message = "Event name is required")
  private String name;

  private String imageUrl;

  @NotNull(message = "Date is required")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotNull(message = "Start time is required")
  @JsonFormat(pattern = "hh:mm a")
  private LocalTime startTime;

  @NotNull(message = "End time is required")
  @JsonFormat(pattern = "hh:mm a")
  private LocalTime endTime;

  @NotBlank(message = "Location is required")
  private String location;

  @NotBlank(message = "Category is required")
  private String category;

  @NotNull(message = "Capacity is required")
  @Min(value = 1, message = "Capacity must be at least 1")
  private Integer capacity;

  @NotBlank(message = "Description is required and cannot be empty")
  @Size(min = 10, max = 2000, message = "Description must be at least 10 characters")
  private String description;

  @NotNull(message = "Organizer ID is required")
  private Long organizerId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
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
