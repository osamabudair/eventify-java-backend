package com.eventify.training2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object representing event summary cards displayed on home and explore feeds.
 */
public class EventCardDTO {
  private Long id;
  private String name;
  private String imageUrl;
  private String category;

  @JsonFormat(pattern = "MMMM d, yyyy")
  private LocalDate date;

  @JsonFormat(pattern = "hh:mm a")
  private LocalTime startTime;

  @JsonFormat(pattern = "hh:mm a")
  private LocalTime endTime;

  private String organizerName;

  public EventCardDTO(Long id, String name, String imageUrl, String category, LocalDate date, String organizerName) {
    this(id, name, imageUrl, category, date, organizerName, null, null);
  }

  public EventCardDTO(Long id, String name, String imageUrl, String category, LocalDate date, String organizerName, LocalTime startTime, LocalTime endTime) {
    this.id = id;
    this.name = name;
    this.imageUrl = imageUrl;
    this.category = category;
    this.date = date;
    this.organizerName = organizerName;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getImageUrl() { return imageUrl; }
  public String getCategory() { return category; }
  public LocalDate getDate() { return date; }
  public String getOrganizerName() { return organizerName; }
  public LocalTime getStartTime() { return startTime; }
  public LocalTime getEndTime() { return endTime; }
}
