package com.eventify.training2.dto;

import java.time.LocalDate;

public class LiveActivityDTO {
  private String studentName;
  private String eventName;
  private LocalDate appliedOn;

  public LiveActivityDTO(String studentName, String eventName, LocalDate appliedOn) {
    this.studentName = studentName;
    this.eventName = eventName;
    this.appliedOn = appliedOn;
  }

  public String getStudentName() { return studentName; }
  public String getEventName() { return eventName; }
  public LocalDate getAppliedOn() { return appliedOn; }
}
