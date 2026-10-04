package com.eventify.training2.dto;

import com.eventify.training2.model.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class LeaderEventDTO {
  private Long id;
  private String name;

  @JsonFormat(pattern = "d-M-yyyy")
  private LocalDate date;

  @JsonFormat(pattern = "hh:mm a")
  private LocalTime startTime;

  @JsonFormat(pattern = "hh:mm a")
  private LocalTime endTime;

  private long approvedAttendees;
  private int capacity;
  private EventStatus status;

  public LeaderEventDTO(Long id, String name, LocalDate date, LocalTime startTime, LocalTime endTime, long approvedAttendees, int capacity, EventStatus status) {
    this.id = id;
    this.name = name;
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.approvedAttendees = approvedAttendees;
    this.capacity = capacity;
    this.status = status;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public LocalDate getDate() { return date; }
  public LocalTime getStartTime() { return startTime; }
  public LocalTime getEndTime() { return endTime; }
  public long getApprovedAttendees() { return approvedAttendees; }
  public int getCapacity() { return capacity; }
  public EventStatus getStatus() { return status; }
}