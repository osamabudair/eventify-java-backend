package com.eventify.training2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class EventCardDTO {
  private Long id;
  private String name;
  private String imageUrl;
  private String category;

  @JsonFormat(pattern = "MMMM d, yyyy")
  private LocalDate date;

  private String organizerName;

  public EventCardDTO(Long id, String name, String imageUrl, String category, LocalDate date, String organizerName) {
    this.id = id;
    this.name = name;
    this.imageUrl = imageUrl;
    this.category = category;
    this.date = date;
    this.organizerName = organizerName;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getImageUrl() { return imageUrl; }
  public String getCategory() { return category; }
  public LocalDate getDate() { return date; }
  public String getOrganizerName() { return organizerName; }
}
