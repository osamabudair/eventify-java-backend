package com.eventify.training2.sevice;

import com.eventify.training2.model.Event;
import com.eventify.training2.repo.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
  @Autowired
  private final EventRepo eventRepo;

  // Constructor
  public EventService(EventRepo eventRepo) {
    this.eventRepo = eventRepo;
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
  public Event createEvent(Event event) {
    if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("Event title cannot be empty!");
    }
    return eventRepo.save(event);
  }

  // Update an event
  public Event updateEvent(Long id, Event newEventData) {
    Event existingEvent = getEventById(id);

    existingEvent.setTitle(newEventData.getTitle());
    existingEvent.setDescription(newEventData.getDescription());
    existingEvent.setLocation(newEventData.getLocation());

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
