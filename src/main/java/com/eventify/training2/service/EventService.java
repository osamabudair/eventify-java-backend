package com.eventify.training2.service;

import com.eventify.training2.dto.EventCardDTO;
import com.eventify.training2.dto.EventRequestDTO;
import com.eventify.training2.dto.LeaderEventDTO;
import com.eventify.training2.model.*;
import com.eventify.training2.repo.EventRepo;
import com.eventify.training2.repo.RegistrationRepo;
import com.eventify.training2.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
  private final EventRepo eventRepo;
  private final UserRepo userRepo;
  private final RegistrationRepo registrationRepo;

  // Constructor
  public EventService(EventRepo eventRepo, UserRepo userRepo, RegistrationRepo registrationRepo) {
    this.eventRepo = eventRepo;
    this.userRepo = userRepo;
    this.registrationRepo = registrationRepo;
  }

  // Get all events
  public List<Event> getAllEvents() {
    return eventRepo.findAll();
  }

  // Get an event by id
  public Event getEventById(Long id) {
    return eventRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
  }

  // Create an event
  public Event createEvent(EventRequestDTO dto) {
    User organizer = userRepo.findById(dto.getOrganizerId())
            .orElseThrow(() -> new RuntimeException("Organizer not found with id: " + dto.getOrganizerId()));

    if (organizer.getRole() != Role.CLUB_LEADER){
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

  // Update an event
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

  // Delete an event
  @Transactional
  public void deleteEvent(Long id) {
    if (!eventRepo.existsById(id)) {
      throw new RuntimeException("Event not found with id: " + id);
    }
    registrationRepo.deleteByEventId(id);
    eventRepo.deleteById(id);
  }

  // Get Events By Organizer
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

  // Get Events By Organizer
  public List<Event> getEventsByLocation(String location){
    return eventRepo.findByLocationIgnoreCase(location);
  }

  public List<EventCardDTO> exploreEvents(String keyword, String category, Long organizerId) {
    return eventRepo.exploreEvents(keyword, category, organizerId)
      .stream()
      .map(e -> new EventCardDTO(
          e.getId(),
          e.getName(),
          e.getImageUrl(),
          e.getCategory(),
          e.getDate(),
          e.getOrganizer().getName()
      ))
      .collect(Collectors.toList());
  }

  public List<EventCardDTO> getUpcomingHighlights() {
    return eventRepo.findTop3ByStatusOrderByDateAsc(EventStatus.ACTIVE)
      .stream()
      .map(e -> new EventCardDTO(
          e.getId(),
          e.getName(),
          e.getImageUrl(),
          e.getCategory(),
          e.getDate(),
          e.getOrganizer().getName()
      ))
      .collect(Collectors.toList());
  }
}
