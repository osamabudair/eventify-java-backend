package com.eventify.training2.service;

import com.eventify.training2.dto.LeaderDashboardDTO;
import com.eventify.training2.dto.LeaderEventDTO;
import com.eventify.training2.dto.LiveActivityDTO;
import com.eventify.training2.model.Event;
import com.eventify.training2.model.Registration;
import com.eventify.training2.model.RegistrationStatus;
import com.eventify.training2.repo.EventRepo;
import com.eventify.training2.repo.RegistrationRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderDashboardService {
  private final EventRepo eventRepo;
  private final RegistrationRepo registrationRepo;

  public LeaderDashboardService(EventRepo eventRepo, RegistrationRepo registrationRepo) {
    this.eventRepo = eventRepo;
    this.registrationRepo = registrationRepo;
  }

  public LeaderDashboardDTO getLeaderDashboardData(Long leaderId) {

    // count Total Events Total, Registrations and Pending Requests
    long totalEvents = eventRepo.countByOrganizerId(leaderId);
    long totalRegistrations = registrationRepo.countByEventOrganizerId(leaderId);
    long pendingRequests = registrationRepo.countByEventOrganizerIdAndStatus(leaderId, RegistrationStatus.PENDING);

    List<Event> events = eventRepo.findByOrganizerId(leaderId);
    List<LeaderEventDTO> recentEvents = events.stream().map(event -> {
      long approvedCount = registrationRepo.countByEventIdAndStatus(event.getId(), RegistrationStatus.APPROVED);
      return new LeaderEventDTO(
          event.getId(),
          event.getName(),
          event.getDate(),
          approvedCount,
          event.getCapacity(),
          event.getStatus()
      );
    }).collect(Collectors.toList());

    // live activity
    List<Registration> recentRegistrations = registrationRepo.findTop5ByEventOrganizerIdOrderByAppliedOnDesc(leaderId);
    List<LiveActivityDTO> liveActivities = recentRegistrations.stream().map(reg ->
      new LiveActivityDTO(
          reg.getStudent().getName(),
          reg.getEvent().getName(),
          reg.getAppliedOn()
      )
    ).collect(Collectors.toList());

    return new LeaderDashboardDTO(totalEvents, totalRegistrations, pendingRequests, recentEvents, liveActivities);
  }
}
