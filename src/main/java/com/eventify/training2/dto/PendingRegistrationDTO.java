package com.eventify.training2.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a pending or approved registration item in leader dashboards.
 */
public class PendingRegistrationDTO {
  private Long registrationId;
  private String studentName;
  private String studentEmail;
  private String eventName;
  private LocalDate appliedOn;

  public PendingRegistrationDTO(Long registrationId, String studentName, String eventName, LocalDate appliedOn) {
    this.registrationId = registrationId;
    this.studentName = studentName;
    this.eventName = eventName;
    this.appliedOn = appliedOn;
    this.studentEmail = (eventName != null && eventName.contains("@")) ? eventName : null;
  }

  public PendingRegistrationDTO(Long registrationId, String studentName, String studentEmail, String eventName, LocalDate appliedOn) {
    this.registrationId = registrationId;
    this.studentName = studentName;
    this.studentEmail = studentEmail;
    this.eventName = eventName;
    this.appliedOn = appliedOn;
  }

  public Long getRegistrationId() {
    return registrationId;
  }

  public String getStudentName() {
    return studentName;
  }

  public String getStudentEmail() {
    return studentEmail;
  }

  public String getEventName() {
    return eventName;
  }

  public LocalDate getAppliedOn() {
    return appliedOn;
  }
}
