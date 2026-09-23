package com.eventify.training2.controller;

import com.eventify.training2.model.Event;
import com.eventify.training2.sevice.EventService;
import org.springframework.beans.factory.annotation.Autowired;
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

  @GetMapping()
  public List<Event> getAllEvents(){
    return eventService.getAllEvents();
  }

  @GetMapping("/{id}")
  public Event getEventById(@PathVariable Long id){
    return eventService.getEventById(id);
  }

  @PostMapping()
  public Event createEvent(@RequestBody Event event){
    return eventService.createEvent(event);
  }

  @PutMapping("/{id}")
  public Event updateEvent(@PathVariable Long id, @RequestBody Event event){
    return eventService.updateEvent(id, event);
  }

  @DeleteMapping("/{id}")
  public String deleteEvent(@PathVariable Long id){
    eventService.deleteEvent(id);
    return "Event with id " + id + " has been deleted successfully!";
  }

  @GetMapping("/user/{userId}")
  public List<Event> getEventsByUser(@PathVariable Long userId) {
    return eventService.getEventsByOrganizer(userId);
  }

  @GetMapping("/search")
  public List<Event> searchByLocation(@RequestParam String location) {
    return eventService.getEventsByLocation(location);
  }
}
