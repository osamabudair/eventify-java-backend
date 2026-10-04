package com.eventify.training2.dto;

import java.time.LocalDateTime;

public class LiveActivityDTO {
  private Long id;
  private String studentName;
  private String eventName;
  private LocalDateTime appliedOn;

  public LiveActivityDTO(Long id, String studentName, String eventName, LocalDateTime appliedOn) {
    this.id = id;
    this.studentName = studentName;
    this.eventName = eventName;
    this.appliedOn = appliedOn;
  }

  public Long getId() { return id; }
  public String getStudentName() { return studentName; }
  public String getEventName() { return eventName; }
  public LocalDateTime getAppliedOn() { return appliedOn; }
}