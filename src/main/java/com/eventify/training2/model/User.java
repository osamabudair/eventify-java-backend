package com.eventify.training2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Event> events = new ArrayList<>();

  // Constructors
  public User(){ }

  public User(String name, String email) {
    this.name = name;
    this.email = email;
  }

  // Setter and Getter
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }
}
