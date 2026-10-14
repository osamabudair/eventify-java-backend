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
      final String normalizedEmail = adminEmail != null ? adminEmail.trim().toLowerCase() : "admin@eventify.com";

      userRepo.findByEmailIgnoreCase(normalizedEmail).ifPresentOrElse(
          existingUser -> {
            boolean updated = false;
            // Ensure email is saved in normalized format
            if (!normalizedEmail.equals(existingUser.getEmail())) {
              existingUser.setEmail(normalizedEmail);
              updated = true;
            }
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
              log.info("Admin user verified and updated with credentials from config: {}", normalizedEmail);
            }
          },
          () -> {
            User admin = new User();
            admin.setName(adminName != null ? adminName.trim() : "System Administrator");
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setApproved(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepo.save(admin);
            log.info("Admin user initialized successfully: {}", normalizedEmail);
          }
      );
    } catch (Exception e) {
      log.warn("AdminUserInitializer warning: {}", e.getMessage());
    }
  }
}

