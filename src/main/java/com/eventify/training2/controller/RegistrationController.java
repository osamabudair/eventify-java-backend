package com.eventify.training2.controller;

import com.eventify.training2.dto.PendingRegistrationDTO;
import com.eventify.training2.dto.StudentDashboardDTO;
import com.eventify.training2.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing event applications, approvals, cancellations, and ticket registries.
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {
  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  /**
   * Submit student application for an event.
   */
  @PostMapping("/apply/{eventId}/student/{studentId}")
  public ResponseEntity<String> applyForEvent(@PathVariable Long eventId, @PathVariable Long studentId) {
    return ResponseEntity.ok(registrationService.applyForEvent(eventId, studentId));
  }

  /**
   * Retrieve all pending registrations for a club leader's events.
   */
  @GetMapping("/leader/{leaderId}/pending")
  public ResponseEntity<List<PendingRegistrationDTO>> getPendingRegistrations(@PathVariable Long leaderId) {
    return ResponseEntity.ok(registrationService.getPendingRegistrationsForLeader(leaderId));
  }

  /**
   * Approve a pending student registration.
   */
  @PutMapping("/{registrationId}/approve")
  public ResponseEntity<String> approveRegistration(@PathVariable Long registrationId) {
    return ResponseEntity.ok(registrationService.approveRegistration(registrationId));
  }

  /**
   * Reject a pending student registration.
   */
  @PutMapping("/{registrationId}/reject")
  public ResponseEntity<String> rejectRegistration(@PathVariable Long registrationId) {
    return ResponseEntity.ok(registrationService.rejectRegistration(registrationId));
  }

  /**
   * Cancel an existing student registration.
   */
  @PutMapping("/{registrationId}/cancel")
  public ResponseEntity<String> cancelRegistration(@PathVariable Long registrationId) {
    return ResponseEntity.ok(registrationService.cancelRegistration(registrationId));
  }

  /**
   * Retrieve tickets and statistics for a student's dashboard.
   */
  @GetMapping("/student/{studentId}/dashboard")
  public ResponseEntity<StudentDashboardDTO> getStudentDashboard(@PathVariable Long studentId) {
    return ResponseEntity.ok(registrationService.getStudentDashboardData(studentId));
  }

  /**
   * Retrieve approved attendees for a specific event.
   */
  @GetMapping("/event/{eventId}/approved")
  public ResponseEntity<List<PendingRegistrationDTO>> getApprovedAttendees(@PathVariable Long eventId) {
    return ResponseEntity.ok(registrationService.getApprovedAttendeesForEvent(eventId));
  }

  @GetMapping("/event/{eventId}/count")
  public ResponseEntity<Long> getApprovedAttendeeCount(@PathVariable Long eventId) {
    return ResponseEntity.ok(registrationService.getApprovedAttendeeCount(eventId));
  }
}
