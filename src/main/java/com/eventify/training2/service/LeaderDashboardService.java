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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service aggregating metrics, active events, and recent registration activity for club leader dashboards.
 */
@Service
@Transactional(readOnly = true)
public class LeaderDashboardService {
  private final EventRepo eventRepo;
  private final RegistrationRepo registrationRepo;

  public LeaderDashboardService(EventRepo eventRepo, RegistrationRepo registrationRepo) {
    this.eventRepo = eventRepo;
    this.registrationRepo = registrationRepo;
  }

  /**
   * Aggregate high-level statistics and lists for the leader overview panel.
   *
   * @param leaderId Club leader ID
   * @return LeaderDashboardDTO containing statistics, recent events, and live registration activity
   */
  public LeaderDashboardDTO getLeaderDashboardData(Long leaderId) {
    long totalEvents = eventRepo.countByOrganizerId(leaderId);
    long totalRegistrations = registrationRepo.countByEventOrganizerIdAndStatus(leaderId, RegistrationStatus.APPROVED);
    long pendingRequests = registrationRepo.countByEventOrganizerIdAndStatus(leaderId, RegistrationStatus.PENDING);

    List<Event> events = eventRepo.findByOrganizerId(leaderId);
    List<LeaderEventDTO> recentEvents = events.stream().map(event -> {
      long approvedCount = registrationRepo.countByEventIdAndStatus(event.getId(), RegistrationStatus.APPROVED);
      return new LeaderEventDTO(
              event.getId(),
              event.getName(),
              event.getDate(),
              event.getStartTime(),
              event.getEndTime(),
              approvedCount,
              event.getCapacity(),
              event.getStatus()
      );
    }).collect(Collectors.toList());

    List<Registration> recentRegistrations = registrationRepo.findTop5ByEventOrganizerIdOrderByAppliedOnDesc(leaderId);
    List<LiveActivityDTO> liveActivities = recentRegistrations.stream().map(reg ->
            new LiveActivityDTO(
                    reg.getId(),
                    reg.getStudent() != null ? reg.getStudent().getName() : "Student",
                    reg.getEvent() != null ? reg.getEvent().getName() : "Event",
                    reg.getAppliedOn()
            )
    ).collect(Collectors.toList());

    return new LeaderDashboardDTO(totalEvents, totalRegistrations, pendingRequests, recentEvents, liveActivities);
  }
}