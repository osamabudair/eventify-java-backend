package com.eventify.training2.dto;

import com.eventify.training2.model.RegistrationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class StudentDashboardDTO {
  private long approvedTicketsCount;
  private long pendingRequestsCount;

  private List<TicketDetailDTO> tickets;

  public StudentDashboardDTO(long approvedTicketsCount, long pendingRequestsCount, List<TicketDetailDTO> tickets) {
    this.approvedTicketsCount = approvedTicketsCount;
    this.pendingRequestsCount = pendingRequestsCount;
    this.tickets = tickets;
  }

  public long getApprovedTicketsCount() { return approvedTicketsCount; }
  public long getPendingRequestsCount() { return pendingRequestsCount; }
  public List<TicketDetailDTO> getTickets() { return tickets; }

  public static class TicketDetailDTO {
    private Long registrationId;
    private String ticketNumber;
    private Long eventId;
    private String eventName;
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "hh:mm a")
    private LocalTime startTime;

    @JsonFormat(pattern = "hh:mm a")
    private LocalTime endTime;

    private RegistrationStatus status;
    private LocalDate appliedOn;

    public TicketDetailDTO(Long registrationId, Long eventId, String eventName,
                           String location, LocalDate date, LocalTime startTime,
                           LocalTime endTime, RegistrationStatus status, LocalDate appliedOn) {
      this.registrationId = registrationId;
      this.ticketNumber = "#TK" + String.format("%04d", registrationId);
      this.eventId = eventId;
      this.eventName = eventName;
      this.location = location;
      this.date = date;
      this.startTime = startTime;
      this.endTime = endTime;
      this.status = status;
      this.appliedOn = appliedOn;
    }

    public Long getRegistrationId() { return registrationId; }
    public String getTicketNumber() { return ticketNumber; }
    public Long getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getLocation() { return location; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public RegistrationStatus getStatus() { return status; }
    public LocalDate getAppliedOn() { return appliedOn; }
  }
}
