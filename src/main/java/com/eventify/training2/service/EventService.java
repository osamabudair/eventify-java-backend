package com.eventify.training2.service;

import com.eventify.training2.dto.EventCardDTO;
import com.eventify.training2.dto.EventDetailsDTO;
import com.eventify.training2.dto.EventRequestDTO;
import com.eventify.training2.dto.LeaderEventDTO;
import com.eventify.training2.model.*;
import com.eventify.training2.repo.EventRepo;
import com.eventify.training2.repo.RegistrationRepo;
import com.eventify.training2.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing campus event creation, discovery, updates, deletion, and attendee tracking.
 */
@Service
@Transactional(readOnly = true)
public class EventService {
  private final EventRepo eventRepo;
  private final UserRepo userRepo;
  private final RegistrationRepo registrationRepo;

  public EventService(EventRepo eventRepo, UserRepo userRepo, RegistrationRepo registrationRepo) {
    this.eventRepo = eventRepo;
    this.userRepo = userRepo;
    this.registrationRepo = registrationRepo;
  }

  /**
   * Retrieve all events stored in the system.
   */
  public List<Event> getAllEvents() {
    return eventRepo.findAll();
  }

  /**
   * Look up a single event by primary key ID.
   */
  public Event getEventById(Long id) {
    return eventRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
  }

  public EventDetailsDTO getEventDetails(Long id) {
    Event event = getEventById(id);
    long approvedCount = registrationRepo.countByEventIdAndStatus(id, RegistrationStatus.APPROVED);
    return new EventDetailsDTO(
      event.getId(),
      event.getName(),
      event.getImageUrl(),
      event.getDate(),
      event.getStartTime(),
      event.getEndTime(),
      event.getLocation(),
      event.getCategory(),
      event.getCapacity(),
      event.getDescription(),
      event.getOrganizer(),
      event.getOrganizer() != null ? event.getOrganizer().getName() : null,
      event.getStatus(),
      approvedCount,
      approvedCount
    );
  }

  /**
   * Create and publish a new event by an authorized club leader.
   */
  @Transactional
  public Event createEvent(EventRequestDTO dto) {
    User organizer = userRepo.findById(dto.getOrganizerId())
            .orElseThrow(() -> new RuntimeException("Organizer not found with id: " + dto.getOrganizerId()));

    if (organizer.getRole() != Role.CLUB_LEADER) {
      throw new RuntimeException("Access denied: Only club leaders can create events!");
    }

    Event event = new Event();
    event.setName(dto.getName());
    event.setImageUrl(dto.getImageUrl());
    event.setDate(dto.getDate());
    event.setStartTime(dto.getStartTime());
    event.setEndTime(dto.getEndTime());
    event.setLocation(dto.getLocation());
    event.setCategory(dto.getCategory());
    event.setCapacity(dto.getCapacity());
    event.setDescription(dto.getDescription());
    event.setOrganizer(organizer);

    return eventRepo.save(event);
  }

  /**
   * Update existing event metadata.
   */
  @Transactional
  public Event updateEvent(Long id, EventRequestDTO dto) {
    Event existingEvent = getEventById(id);

    existingEvent.setName(dto.getName());
    existingEvent.setImageUrl(dto.getImageUrl());
    existingEvent.setDate(dto.getDate());
    existingEvent.setStartTime(dto.getStartTime());
    existingEvent.setEndTime(dto.getEndTime());
    existingEvent.setLocation(dto.getLocation());
    existingEvent.setCategory(dto.getCategory());
    existingEvent.setCapacity(dto.getCapacity());
    existingEvent.setDescription(dto.getDescription());

    return eventRepo.save(existingEvent);
  }

  /**
   * Remove an event and cascade deletion to associated registration requests.
   */
  @Transactional
  public void deleteEvent(Long id) {
    if (!eventRepo.existsById(id)) {
      throw new RuntimeException("Event not found with id: " + id);
    }
    registrationRepo.deleteByEventId(id);
    eventRepo.deleteById(id);
  }

  /**
   * Retrieve all events created by a specific club leader organizer.
   */
  public List<LeaderEventDTO> getEventsByOrganizer(Long organizerId) {
    List<Event> events = eventRepo.findByOrganizerId(organizerId);
    return events.stream().map(event -> {
      long approvedCount = registrationRepo.countByEventIdAndStatus(event.getId(), RegistrationStatus.APPROVED);
      return new LeaderEventDTO(
              event.getId(),
              event.getName(),
              event.getDate(),
              event.getStartTime(),
              event.getEndTime(),
              approvedCount,
              event.getCapacity(),
              event.getStatus()
      );
    }).collect(Collectors.toList());
  }

  /**
   * Search events filtered by location.
   */
  public List<Event> getEventsByLocation(String location) {
    return eventRepo.findByLocationIgnoreCase(location);
  }

  /**
   * Query events for the public Explore Events catalog with optional filters.
   * Includes startTime and endTime directly to prevent frontend N+1 queries.
   */
  public List<EventCardDTO> exploreEvents(String keyword, String category, Long organizerId) {
    return eventRepo.exploreEvents(keyword, category, organizerId)
      .stream()
      .map(e -> new EventCardDTO(
          e.getId(),
          e.getName(),
          e.getImageUrl(),
          e.getCategory(),
          e.getDate(),
          e.getOrganizer().getName(),
          e.getStartTime(),
          e.getEndTime()
      ))
      .collect(Collectors.toList());
  }

  /**
   * Query top upcoming active events for the home feed highlights.
   */
  public List<EventCardDTO> getUpcomingHighlights() {
    return eventRepo.findTop3ByStatusOrderByDateAsc(EventStatus.ACTIVE)
      .stream()
      .map(e -> new EventCardDTO(
          e.getId(),
          e.getName(),
          e.getImageUrl(),
          e.getCategory(),
          e.getDate(),
          e.getOrganizer().getName(),
          e.getStartTime(),
          e.getEndTime()
      ))
      .collect(Collectors.toList());
  }
}
