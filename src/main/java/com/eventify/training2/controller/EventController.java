package com.eventify.training2.controller;

import com.eventify.training2.dto.EventCardDTO;
import com.eventify.training2.dto.EventRequestDTO;
import com.eventify.training2.model.Event;
import com.eventify.training2.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

  @Autowired
  private final EventService eventService;

  public EventController(EventService eventService){
    this.eventService = eventService;
  }

  // 200 OK
  @GetMapping()
  public ResponseEntity<List<Event>> getAllEvents(){
    return ResponseEntity.ok(eventService.getAllEvents());
  }

  // 200 OK
  @GetMapping("/{id}")
  public ResponseEntity<Event> getEventById(@PathVariable Long id){
    return ResponseEntity.ok(eventService.getEventById(id));
  }

  // 201 CREATED
  @PostMapping
  public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequestDTO eventDTO) {
    Event createdEvent = eventService.createEvent(eventDTO);
    return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
  }

  // 200 OK
  @PutMapping("/{id}")
  public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDTO eventDTO) {
    Event updated = eventService.updateEvent(id, eventDTO);
    return ResponseEntity.ok(updated);
  }

  // 204 NO CONTENT
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
    eventService.deleteEvent(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user/{userId}")
  public List<Event> getEventsByUser(@PathVariable Long userId) {
    return eventService.getEventsByOrganizer(userId);
  }

  @GetMapping("/search")
  public List<Event> searchByLocation(@RequestParam String location) {
    return eventService.getEventsByLocation(location);
  }

  @GetMapping("/explore")
  public ResponseEntity<List<EventCardDTO>> exploreEvents(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String category,
          @RequestParam(required = false) Long organizerId) {

    List<EventCardDTO> events = eventService.exploreEvents(keyword, category, organizerId);
    return ResponseEntity.ok(events);
  }

  @GetMapping("/upcoming-highlights")
  public ResponseEntity<List<EventCardDTO>> getUpcomingHighlights() {
    return ResponseEntity.ok(eventService.getUpcomingHighlights());
  }
}
