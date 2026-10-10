package com.eventify.training2.config;

import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initializes and manages the hardcoded admin account based on application.properties.
 * Ensures the administrator always has authorized access with Role.ADMIN.
 */
@Component
@Order(1)
public class AdminUserInitializer implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;

  @Value("${admin.email:admin@eventify.com}")
  private String adminEmail;

  @Value("${admin.password:admin123456}")
  private String adminPassword;

  @Value("${admin.name:System Administrator}")
  private String adminName;

  public AdminUserInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    try {
      userRepo.findByEmail(adminEmail).ifPresentOrElse(
          existingUser -> {
            boolean updated = false;
            if (existingUser.getRole() != Role.ADMIN) {
              existingUser.setRole(Role.ADMIN);
              updated = true;
            }
            if (!Boolean.TRUE.equals(existingUser.getApproved())) {
              existingUser.setApproved(true);
              updated = true;
            }
            if (!passwordEncoder.matches(adminPassword, existingUser.getPassword())) {
              existingUser.setPassword(passwordEncoder.encode(adminPassword));
              updated = true;
            }
            if (updated) {
              userRepo.save(existingUser);
              log.info("Admin user verified and updated with credentials from config: {}", adminEmail);
            }
          },
          () -> {
            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setApproved(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepo.save(admin);
            log.info("Admin user initialized successfully: {}", adminEmail);
          }
      );
    } catch (Exception e) {
      log.warn("AdminUserInitializer warning: {}", e.getMessage());
    }
  }
}

