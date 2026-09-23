package com.eventify.training2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "events")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String location;
  private String description;

  @ManyToOne
  @JoinColumn(name = "organizer_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User organizer;

  public Event(){}

  public Event(String title, String location, String description){
    this.title = title;
    this.description = description;
    this.location = location;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getOrganizer() {
    return organizer;
  }

  public void setOrganizer(User organizer) {
    this.organizer = organizer;
  }
}
