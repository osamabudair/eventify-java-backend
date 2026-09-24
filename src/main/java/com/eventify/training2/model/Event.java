package com.eventify.training2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String imageUrl;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  @JsonFormat(pattern = "hh:mm a")
  private LocalTime startTime;

  @Column(nullable = false)
  @JsonFormat(pattern = "hh:mm a")
  private LocalTime endTime;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false)
  private String category;

  @Column(nullable = false)
  private Integer capacity;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  @ManyToOne
  @JoinColumn(name = "organizer_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User organizer;

  public Event(){}


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }
}
