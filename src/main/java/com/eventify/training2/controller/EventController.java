package com.eventify.training2.controller;

import com.eventify.training2.dto.EventCardDTO;
import com.eventify.training2.dto.EventRequestDTO;
import com.eventify.training2.dto.LeaderEventDTO;
import com.eventify.training2.model.Event;
import com.eventify.training2.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints for event creation, updates, public catalog discovery, and highlights.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

  private final EventService eventService;

  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  /**
   * Retrieve list of all events.
   */
  @GetMapping
  public ResponseEntity<List<Event>> getAllEvents() {
    return ResponseEntity.ok(eventService.getAllEvents());
  }

  /**
   * Retrieve detailed information for a specific event by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Event> getEventById(@PathVariable Long id) {
    return ResponseEntity.ok(eventService.getEventById(id));
  }

  /**
   * Create and publish a new event.
   */
  @PostMapping
  public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequestDTO eventDTO) {
    Event createdEvent = eventService.createEvent(eventDTO);
    return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
  }

  /**
   * Update an existing event by ID.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDTO eventDTO) {
    Event updated = eventService.updateEvent(id, eventDTO);
    return ResponseEntity.ok(updated);
  }

  /**
   * Delete an event by ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
    eventService.deleteEvent(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieve events created by a specific user/organizer.
   */
  @GetMapping({"/user/{userId}", "/organizer/{userId}"})
  public ResponseEntity<List<LeaderEventDTO>> getEventsByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(eventService.getEventsByOrganizer(userId));
  }

  /**
   * Search events filtered by location name.
   */
  @GetMapping("/search")
  public List<Event> searchByLocation(@RequestParam String location) {
    return eventService.getEventsByLocation(location);
  }

  /**
   * Explore events with keyword, category, and organizer filtering.
   */
  @GetMapping("/explore")
  public ResponseEntity<List<EventCardDTO>> exploreEvents(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String category,
          @RequestParam(required = false) Long organizerId) {

    List<EventCardDTO> events = eventService.exploreEvents(keyword, category, organizerId);
    return ResponseEntity.ok(events);
  }

  /**
   * Retrieve upcoming active events for the home highlights carousel.
   */
  @GetMapping("/upcoming-highlights")
  public ResponseEntity<List<EventCardDTO>> getUpcomingHighlights() {
    return ResponseEntity.ok(eventService.getUpcomingHighlights());
  }
}
