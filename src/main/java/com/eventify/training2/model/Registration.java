package com.eventify.training2.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @Column(nullable = false)
  private LocalDateTime appliedOn = LocalDateTime.now();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RegistrationStatus status;

  public Registration() {
  }

  public Registration(User student, Event event) {
    this.student = student;
    this.event = event;
    this.appliedOn = LocalDateTime.now();
    this.status = RegistrationStatus.PENDING;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public User getStudent() {
    return student;
  }

  public void setStudent(User student) {
    this.student = student;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public LocalDateTime getAppliedOn() {
    return appliedOn;
  }

  public void setAppliedOn(LocalDateTime appliedOn) {
    this.appliedOn = appliedOn;
  }

  public RegistrationStatus getStatus() {
    return status;
  }

  public void setStatus(RegistrationStatus status) {
    this.status = status;
  }
}
