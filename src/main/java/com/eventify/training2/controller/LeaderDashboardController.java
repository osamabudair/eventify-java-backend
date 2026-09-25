package com.eventify.training2.controller;

import com.eventify.training2.dto.LeaderDashboardDTO;
import com.eventify.training2.service.LeaderDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leader")
@CrossOrigin(origins = "*")
public class LeaderDashboardController {

  private final LeaderDashboardService leaderDashboardService;

  public LeaderDashboardController(LeaderDashboardService leaderDashboardService) {
    this.leaderDashboardService = leaderDashboardService;
  }

  @GetMapping("/{leaderId}/dashboard")
  public ResponseEntity<LeaderDashboardDTO> getDashboard(@PathVariable Long leaderId) {
    return ResponseEntity.ok(leaderDashboardService.getLeaderDashboardData(leaderId));
  }
}
