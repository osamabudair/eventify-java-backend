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

  @Column(nullable = false, length = 512)
  private String password;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "approved")
  private Boolean approved = true;

  @Column(name = "created_at")
  private java.time.LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = java.time.LocalDateTime.now();
    }
    if (approved == null) {
      approved = (role != Role.CLUB_LEADER);
    }
  }

  @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Event> events = new ArrayList<>();

  // Constructors
  public User(){ }

  public User(String name, String email, String password, Role role) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
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

  public String getPassword() { return password; }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public String getProfileImageUrl() { return profileImageUrl; }

  public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

  public Boolean getApproved() {
    if (approved == null) {
      return role != Role.CLUB_LEADER;
    }
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }

  public java.time.LocalDateTime getCreatedAt() {
    return createdAt != null ? createdAt : java.time.LocalDateTime.now();
  }

  public void setCreatedAt(java.time.LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
