package com.eventify.training2.repo;

import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Repository for User entities.
 * Supports case-insensitive email lookups, role-based queries, and admin approval workflows.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

  /**
   * Look up a user by exact email.
   *
   * @param email User email string
   * @return Optional containing the matched User if present
   */
  Optional<User> findByEmail(String email);

  /**
   * Look up a user by email, ignoring casing (e.g. OsaMa@gmail.com == osama@gmail.com).
   *
   * @param email User email string
   * @return Optional containing the matched User if present
   */
  Optional<User> findByEmailIgnoreCase(String email);

  /**
   * Check if a user exists with the exact email.
   *
   * @param email User email string
   * @return True if user exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Check if a user exists with the specified email, ignoring casing.
   *
   * @param email User email string
   * @return True if user exists, false otherwise
   */
  boolean existsByEmailIgnoreCase(String email);

  /**
   * Find all users matching a specific role.
   *
   * @param role User security role
   * @return List of matching users
   */
  List<User> findByRole(Role role);

  /**
   * Find users matching role and approval status, sorted newest first.
   *
   * @param role User security role
   * @param approved Approval flag
   * @return List of users
   */
  List<User> findByRoleAndApprovedOrderByCreatedAtDesc(Role role, Boolean approved);

  /**
   * Retrieve club leaders awaiting admin approval.
   *
   * @param role Role filter (typically CLUB_LEADER)
   * @return List of pending leader users
   */
  @Query("SELECT u FROM User u WHERE u.role = :role AND (u.approved = false OR u.approved IS NULL) ORDER BY u.createdAt DESC")
  List<User> findPendingLeaders(@Param("role") Role role);

  /**
   * Count club leaders awaiting admin verification.
   *
   * @param role Role filter (typically CLUB_LEADER)
   * @return Total pending count
   */
  @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND (u.approved = false OR u.approved IS NULL)")
  long countPendingLeaders(@Param("role") Role role);

  /**
   * Count total users by role.
   *
   * @param role User security role
   * @return Count of users
   */
  long countByRole(Role role);

  /**
   * Count users by role and approval status.
   *
   * @param role User security role
   * @param approved Approval flag
   * @return Count of users
   */
  long countByRoleAndApproved(Role role, Boolean approved);
}
