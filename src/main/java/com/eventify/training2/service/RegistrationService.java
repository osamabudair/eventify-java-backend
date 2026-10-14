package com.eventify.training2.service;

import com.eventify.training2.dto.PendingRegistrationDTO;
import com.eventify.training2.dto.StudentDashboardDTO;
import com.eventify.training2.model.*;
import com.eventify.training2.repo.EventRepo;
import com.eventify.training2.repo.RegistrationRepo;
import com.eventify.training2.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing student event registrations, ticket issuance, leader approval flows,
 * and capacity constraints.
 */
@Service
@Transactional(readOnly = true)
public class RegistrationService {

  private final RegistrationRepo registrationRepo;
  private final EventRepo eventRepo;
  private final UserRepo userRepo;

  public RegistrationService(RegistrationRepo registrationRepo, EventRepo eventRepo, UserRepo userRepo) {
    this.registrationRepo = registrationRepo;
    this.eventRepo = eventRepo;
    this.userRepo = userRepo;
  }

  /**
   * Submit a student registration application for an event.
   * Enforces capacity limits and prevents duplicate applications.
   *
   * @param eventId Target event ID
   * @param studentId Applying student ID
   * @return Success message string
   */
  @Transactional
  public String applyForEvent(Long eventId, Long studentId) {
    User student = userRepo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    if (student.getRole() != Role.STUDENT) {
      throw new RuntimeException("Only students can apply for events!");
    }

    Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

    long approvedCount = registrationRepo.countByEventIdAndStatus(eventId, RegistrationStatus.APPROVED);
    if (approvedCount >= event.getCapacity()) {
      throw new RuntimeException("Application closed: Event capacity is full!");
    }

    if (registrationRepo.existsByStudentIdAndEventId(studentId, eventId)) {
      throw new RuntimeException("You have already applied for this event!");
    }

    Registration registration = new Registration(student, event);
    registrationRepo.save(registration);

    return "Application submitted successfully and is pending approval!";
  }

  /**
   * Retrieve all pending registration applications for a club leader's events.
   *
   * @param leaderId Club leader ID
   * @return List of pending registration DTOs
   */
  public List<PendingRegistrationDTO> getPendingRegistrationsForLeader(Long leaderId) {
    return registrationRepo.findByEventOrganizerIdAndStatus(leaderId, RegistrationStatus.PENDING)
        .stream()
        .map(reg -> new PendingRegistrationDTO(
            reg.getId(),
            reg.getStudent() != null ? reg.getStudent().getName() : "Student",
            reg.getStudent() != null ? reg.getStudent().getEmail() : "",
            reg.getEvent() != null ? reg.getEvent().getName() : "Event",
            reg.getAppliedOn() != null ? reg.getAppliedOn().toLocalDate() : java.time.LocalDate.now()
        ))
        .collect(Collectors.toList());
  }

  /**
   * Approve a pending student registration, enforcing remaining event capacity.
   *
   * @param registrationId Target registration ID
   * @return Confirmation message
   */
  @Transactional
  public String approveRegistration(Long registrationId) {
    Registration reg = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));

    Event event = reg.getEvent();

    long approvedCount = registrationRepo.countByEventIdAndStatus(event.getId(), RegistrationStatus.APPROVED);

    if (approvedCount >= event.getCapacity()) {
      throw new RuntimeException("Cannot approve: Event is already at full capacity!");
    }

    reg.setStatus(RegistrationStatus.APPROVED);
    registrationRepo.save(reg);

    return "Registration approved successfully!";
  }

  /**
   * Reject a student registration application.
   *
   * @param registrationId Target registration ID
   * @return Rejection confirmation message
   */
  @Transactional
  public String rejectRegistration(Long registrationId) {
    Registration reg = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));
    reg.setStatus(RegistrationStatus.REJECTED);
    registrationRepo.save(reg);
    return "Registration rejected successfully!";
  }

  /**
   * Cancel an active or pending registration.
   *
   * @param registrationId Target registration ID
   * @return Cancellation confirmation message
   */
  @Transactional
  public String cancelRegistration(Long registrationId) {
    Registration reg = registrationRepo.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));

    reg.setStatus(RegistrationStatus.CANCELLED);
    registrationRepo.save(reg);
    return "Registration has been cancelled successfully.";
  }

  /**
   * Retrieve overview metrics and tickets for a student portal.
   *
   * @param studentId Student user ID
   * @return Aggregated student dashboard DTO
   */
  public StudentDashboardDTO getStudentDashboardData(Long studentId) {
    long approved = registrationRepo.countByStudentIdAndStatus(studentId, RegistrationStatus.APPROVED);
    long pending = registrationRepo.countByStudentIdAndStatus(studentId, RegistrationStatus.PENDING);

    List<StudentDashboardDTO.TicketDetailDTO> tickets = registrationRepo
      .findByStudentIdOrderByAppliedOnDesc(studentId)
      .stream()
      .map(reg -> new StudentDashboardDTO.TicketDetailDTO(
          reg.getId(),
          reg.getEvent().getId(),
          reg.getEvent().getName(),
          reg.getEvent().getLocation(),
          reg.getEvent().getDate(),
          reg.getEvent().getStartTime(),
          reg.getEvent().getEndTime(),
          reg.getStatus(),
          reg.getAppliedOn(),
          reg.getEvent().getOrganizer() != null ? reg.getEvent().getOrganizer().getName() : "Club Leader"
      ))
      .collect(Collectors.toList());

    return new StudentDashboardDTO(approved, pending, tickets);
  }

  /**
   * Retrieve list of approved attendees for a specific event.
   *
   * @param eventId Target event ID
   * @return List of approved attendee records
   */
  public List<PendingRegistrationDTO> getApprovedAttendeesForEvent(Long eventId) {
    return registrationRepo.findByEventIdAndStatus(eventId, RegistrationStatus.APPROVED)
      .stream()
      .map(reg -> new PendingRegistrationDTO(
          reg.getId(),
          reg.getStudent() != null ? reg.getStudent().getName() : "Student",
          reg.getStudent() != null ? reg.getStudent().getEmail() : "",
          reg.getEvent() != null ? reg.getEvent().getName() : "",
          reg.getAppliedOn() != null ? reg.getAppliedOn().toLocalDate() : java.time.LocalDate.now()
      ))
      .collect(Collectors.toList());
  }
}
