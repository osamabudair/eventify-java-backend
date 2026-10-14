package com.eventify.training2.repo;

import com.eventify.training2.model.Registration;
import com.eventify.training2.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Data Access Repository for Registration entities.
 * Includes optimized fetch queries to prevent N+1 query overhead and deadlocks.
 */
@Repository
public interface RegistrationRepo extends JpaRepository<Registration, Long> {

  /**
   * Find registrations for a specific organizer by status, eagerly loading student and event.
   */
  @Query("SELECT r FROM Registration r JOIN FETCH r.student s JOIN FETCH r.event e WHERE e.organizer.id = :organizerId AND r.status = :status")
  List<Registration> findByEventOrganizerIdAndStatus(@Param("organizerId") Long organizerId, @Param("status") RegistrationStatus status);

  /**
   * Check if a student has already applied for a specific event.
   */
  boolean existsByStudentIdAndEventId(Long studentId, Long eventId);

  /**
   * Count total registrations for an event matching a specific status.
   */
  long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

  /**
   * Count total registrations for a student matching a specific status.
   */
  long countByStudentIdAndStatus(Long studentId, RegistrationStatus status);

  /**
   * Retrieve all registrations for a student ordered newest first, eagerly fetching the associated event and organizer.
   */
  @Query("SELECT r FROM Registration r JOIN FETCH r.event e LEFT JOIN FETCH e.organizer WHERE r.student.id = :studentId ORDER BY r.appliedOn DESC")
  List<Registration> findByStudentIdOrderByAppliedOnDesc(@Param("studentId") Long studentId);

  /**
   * Count all registrations across events owned by an organizer.
   */
  long countByEventOrganizerId(Long organizerId);

  /**
   * Count registrations across an organizer's events with a specific status.
   */
  long countByEventOrganizerIdAndStatus(Long organizerId, RegistrationStatus status);

  /**
   * Retrieve top 5 recent registrations for an organizer's events, eagerly loading student and event entities.
   */
  @Query("SELECT r FROM Registration r JOIN FETCH r.student s JOIN FETCH r.event e WHERE e.organizer.id = :organizerId ORDER BY r.appliedOn DESC")
  List<Registration> findTop5ByEventOrganizerIdOrderByAppliedOnDesc(@Param("organizerId") Long organizerId);

  /**
   * Find all registrations for a specific event and status, eagerly loading student details.
   */
  @Query("SELECT r FROM Registration r JOIN FETCH r.student s WHERE r.event.id = :eventId AND r.status = :status")
  List<Registration> findByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RegistrationStatus status);

  /**
   * Delete all registrations belonging to an event.
   */
  @Transactional
  @Modifying
  @Query("DELETE FROM Registration r WHERE r.event.id = :eventId")
  void deleteByEventId(@Param("eventId") Long eventId);

  /**
   * Delete all registrations belonging to a student.
   */
  @Transactional
  @Modifying
  @Query("DELETE FROM Registration r WHERE r.student.id = :studentId")
  void deleteByStudentId(@Param("studentId") Long studentId);
}
