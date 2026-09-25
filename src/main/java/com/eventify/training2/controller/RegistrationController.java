package com.eventify.training2.controller;

import com.eventify.training2.dto.PendingRegistrationDTO;
import com.eventify.training2.dto.StudentDashboardDTO;
import com.eventify.training2.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {
  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  // Request student to event
  @PostMapping("/apply/{eventId}/student/{studentId}")
  public ResponseEntity<String> applyForEvent(@PathVariable Long eventId, @PathVariable Long studentId) {
    return ResponseEntity.ok(registrationService.applyForEvent(eventId, studentId));
  }

  // Pending request
  @GetMapping("/leader/{leaderId}/pending")
  public ResponseEntity<List<PendingRegistrationDTO>> getPendingRegistrations(@PathVariable Long leaderId) {
    return ResponseEntity.ok(registrationService.getPendingRegistrationsForLeader(leaderId));
  }

  // approve request
  @PutMapping("/{registrationId}/approve")
  public ResponseEntity<String> approveRegistration(@PathVariable Long registrationId) {
    return ResponseEntity.ok(registrationService.approveRegistration(registrationId));
  }

  // reject request
  @PutMapping("/{registrationId}/reject")
  public ResponseEntity<String> rejectRegistration(@PathVariable Long registrationId) {
    return ResponseEntity.ok(registrationService.rejectRegistration(registrationId));
  }

  // reject student (remove)
  @PutMapping("/{registrationId}/cancel")
  public ResponseEntity<String> cancelRegistration(@PathVariable Long registrationId) {
    return ResponseEntity.ok(registrationService.cancelRegistration(registrationId));
  }

  @GetMapping("/student/{studentId}/dashboard")
  public ResponseEntity<StudentDashboardDTO> getStudentDashboard(@PathVariable Long studentId) {
    return ResponseEntity.ok(registrationService.getStudentDashboardData(studentId));
  }
}
