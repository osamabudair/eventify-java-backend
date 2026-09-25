package com.eventify.training2.dto;

import java.time.LocalDate;

public class PendingRegistrationDTO {
  private Long registrationId;
  private String studentName;
  private String eventName;
  private LocalDate appliedOn;

  public PendingRegistrationDTO(Long registrationId, String studentName, String eventName, LocalDate appliedOn) {
    this.registrationId = registrationId;
    this.studentName = studentName;
    this.eventName = eventName;
    this.appliedOn = appliedOn;
  }

  public Long getRegistrationId() {
    return registrationId;
  }

  public String getStudentName() {
    return studentName;
  }

  public String getEventName() {
    return eventName;
  }

  public LocalDate getAppliedOn() {
    return appliedOn;
  }
}
