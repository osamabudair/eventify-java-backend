package com.eventify.training2.dto;

import com.eventify.training2.model.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class LeaderEventDTO {
  private Long id;
  private String name;

  @JsonFormat(pattern = "MMMM d, yyyy")
  private LocalDate date;

  private long approvedAttendees;
  private int capacity;
  private EventStatus status;

  public LeaderEventDTO(Long id, String name, LocalDate date, long approvedAttendees, int capacity, EventStatus status) {
    this.id = id;
    this.name = name;
    this.date = date;
    this.approvedAttendees = approvedAttendees;
    this.capacity = capacity;
    this.status = status;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public LocalDate getDate() { return date; }
  public long getApprovedAttendees() { return approvedAttendees; }
  public int getCapacity() { return capacity; }
  public EventStatus getStatus() { return status; }
}
