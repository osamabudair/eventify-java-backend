package com.eventify.training2.controller;

import com.eventify.training2.dto.LeaderApprovalDTO;
import com.eventify.training2.model.Event;
import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.EventRepo;
import com.eventify.training2.repo.RegistrationRepo;
import com.eventify.training2.repo.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final UserRepo userRepo;
  private final EventRepo eventRepo;
  private final RegistrationRepo registrationRepo;

  public AdminController(UserRepo userRepo, EventRepo eventRepo, RegistrationRepo registrationRepo) {
    this.userRepo = userRepo;
    this.eventRepo = eventRepo;
    this.registrationRepo = registrationRepo;
  }

  @GetMapping("/pending-leaders")
  public ResponseEntity<List<LeaderApprovalDTO>> getPendingLeaders() {
    List<User> pending = userRepo.findPendingLeaders(Role.CLUB_LEADER);
    List<LeaderApprovalDTO> dtos = pending.stream()
        .map(u -> new LeaderApprovalDTO(
            u.getId(),
            u.getName(),
            u.getEmail(),
            u.getRole().name(),
            u.getApproved(),
            u.getCreatedAt(),
            u.getProfileImageUrl()
        ))
        .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/leaders")
  public ResponseEntity<List<LeaderApprovalDTO>> getAllLeaders() {
    List<User> leaders = userRepo.findByRole(Role.CLUB_LEADER);
    List<LeaderApprovalDTO> dtos = leaders.stream()
        .map(u -> new LeaderApprovalDTO(
            u.getId(),
            u.getName(),
            u.getEmail(),
            u.getRole().name(),
            u.getApproved(),
            u.getCreatedAt(),
            u.getProfileImageUrl()
        ))
        .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
  }

  @Transactional
  @PutMapping("/approve-leader/{id}")
  public ResponseEntity<Map<String, Object>> approveLeader(@PathVariable Long id) {
    User user = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Leader not found with id: " + id));

    user.setApproved(true);
    userRepo.save(user);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Leader approved successfully!");
    response.put("id", user.getId());
    response.put("approved", true);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PutMapping("/reject-leader/{id}")
  public ResponseEntity<Map<String, Object>> rejectLeader(@PathVariable Long id) {
    User user = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Leader not found with id: " + id));

    List<Event> leaderEvents = user.getEvents();

    if (leaderEvents != null && !leaderEvents.isEmpty()) {
      for (Event event : leaderEvents) {
        registrationRepo.deleteByEventId(event.getId());
      }
      eventRepo.deleteAll(leaderEvents);
    }

    // 4. حذف الليدر بأمان
    userRepo.delete(user);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "Leader and all associated records deleted successfully!");
    response.put("id", id);
    response.put("rejected", true);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/stats")
  public ResponseEntity<Map<String, Object>> getAdminStats() {
    long pendingLeaders = userRepo.countPendingLeaders(Role.CLUB_LEADER);
    long approvedLeaders = userRepo.countByRoleAndApproved(Role.CLUB_LEADER, true);
    long totalStudents = userRepo.countByRole(Role.STUDENT);
    long totalUsers = userRepo.count();

    Map<String, Object> stats = new HashMap<>();
    stats.put("pendingLeadersCount", pendingLeaders);
    stats.put("approvedLeadersCount", approvedLeaders);
    stats.put("totalStudentsCount", totalStudents);
    stats.put("totalUsersCount", totalUsers);

    return ResponseEntity.ok(stats);
  }
}