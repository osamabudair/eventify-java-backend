package com.eventify.training2.dto;

import com.eventify.training2.model.EventStatus;
import com.eventify.training2.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventDetailsDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private LocalDate date;

    @JsonFormat(pattern = "hh:mm a")
    private LocalTime startTime;

    @JsonFormat(pattern = "hh:mm a")
    private LocalTime endTime;

    private String location;
    private String category;
    private Integer capacity;
    private String description;
    private User organizer;
    private String organizerName;
    private EventStatus status;
    private long approvedAttendees;
    private long approvedCount;

    public EventDetailsDTO() {}

    public EventDetailsDTO(Long id, String name, String imageUrl, LocalDate date,
                           LocalTime startTime, LocalTime endTime, String location,
                           String category, Integer capacity, String description,
                           User organizer, String organizerName, EventStatus status,
                           long approvedAttendees, long approvedCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.category = category;
        this.capacity = capacity;
        this.description = description;
        this.organizer = organizer;
        this.organizerName = organizerName;
        this.status = status;
        this.approvedAttendees = approvedAttendees;
        this.approvedCount = approvedCount;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public long getApprovedAttendees() {
        return approvedAttendees;
    }

    public void setApprovedAttendees(long approvedAttendees) {
        this.approvedAttendees = approvedAttendees;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }
}

