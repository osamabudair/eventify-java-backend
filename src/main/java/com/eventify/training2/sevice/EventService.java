package com.eventify.training2.sevice;

import com.eventify.training2.dto.EventRequestDTO;
import com.eventify.training2.model.Event;
import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.EventRepo;
import com.eventify.training2.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
  @Autowired
  private final EventRepo eventRepo;

  @Autowired
  private final UserRepo userRepo;

  // Constructor
  public EventService(EventRepo eventRepo, UserRepo userRepo) {
    this.eventRepo = eventRepo;
    this.userRepo = userRepo;
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
  public void deleteEvent(Long id) {
    if (!eventRepo.existsById(id)) {
      throw new RuntimeException("Event not found with id: " + id);
    }
    eventRepo.deleteById(id);
  }

  // Get Events By Organizer
  public List<Event> getEventsByOrganizer(Long userId){
    return eventRepo.findByOrganizerId(userId);
  }

  // Get Events By Organizer
  public List<Event> getEventsByLocation(String location){
    return eventRepo.findByLocationIgnoreCase(location);
  }
}
