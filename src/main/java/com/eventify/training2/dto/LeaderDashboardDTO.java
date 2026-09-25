package com.eventify.training2.dto;

import java.util.List;

public class LeaderDashboardDTO {
  private long totalEvents;
  private long totalRegistrations;
  private long pendingRequests;
  private List<LeaderEventDTO> recentEvents;
  private List<LiveActivityDTO> liveActivities;

  public LeaderDashboardDTO(long totalEvents, long totalRegistrations, long pendingRequests,
                            List<LeaderEventDTO> recentEvents, List<LiveActivityDTO> liveActivities) {
    this.totalEvents = totalEvents;
    this.totalRegistrations = totalRegistrations;
    this.pendingRequests = pendingRequests;
    this.recentEvents = recentEvents;
    this.liveActivities = liveActivities;
  }

  public long getTotalEvents() { return totalEvents; }
  public long getTotalRegistrations() { return totalRegistrations; }
  public long getPendingRequests() { return pendingRequests; }
  public List<LeaderEventDTO> getRecentEvents() { return recentEvents; }
  public List<LiveActivityDTO> getLiveActivities() { return liveActivities; }
}
