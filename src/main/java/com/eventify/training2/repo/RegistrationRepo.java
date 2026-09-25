package com.eventify.training2.repo;

import com.eventify.training2.model.Registration;
import com.eventify.training2.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepo extends JpaRepository<Registration, Long> {

  List<Registration> findByEventOrganizerIdAndStatus(Long organizerId, RegistrationStatus status);

  boolean existsByStudentIdAndEventId(Long studentId, Long eventId);

  long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

  // count approved or pending request
  long countByStudentIdAndStatus(Long studentId, RegistrationStatus status);

  // get all registrations on desc
  List<Registration> findByStudentIdOrderByAppliedOnDesc(Long studentId);

  // count all events to leader
  long countByEventOrganizerId(Long organizerId);

  // pending request for leader
  long countByEventOrganizerIdAndStatus(Long organizerId, RegistrationStatus status);

  List<Registration> findTop5ByEventOrganizerIdOrderByAppliedOnDesc(Long organizerId);

}
