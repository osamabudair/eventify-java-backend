package com.eventify.training2.repo;

import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  List<User> findByRole(Role role);

  List<User> findByRoleAndApprovedOrderByCreatedAtDesc(Role role, Boolean approved);

  @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.role = :role AND (u.approved = false OR u.approved IS NULL) ORDER BY u.createdAt DESC")
  List<User> findPendingLeaders(@org.springframework.data.repository.query.Param("role") Role role);

  @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND (u.approved = false OR u.approved IS NULL)")
  long countPendingLeaders(@org.springframework.data.repository.query.Param("role") Role role);

  long countByRole(Role role);

  long countByRoleAndApproved(Role role, Boolean approved);
}
