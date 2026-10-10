package com.eventify.training2;

import com.eventify.training2.config.CustomUserDetailsService;
import com.eventify.training2.config.JwtPasswordEncoder;
import com.eventify.training2.config.JwtUtils;
import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SecurityAndJwtTest {

  private JwtUtils jwtUtils;
  private JwtPasswordEncoder jwtPasswordEncoder;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private UserRepo userRepo;
  private CustomUserDetailsService userDetailsService;

  @BeforeEach
  void setUp() {
    jwtUtils = new JwtUtils();
    ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
    ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L); // 1 hour

    jwtPasswordEncoder = new JwtPasswordEncoder();
    jwtPasswordEncoder.setPasswordSecret("EventifyJwtSecurePasswordSecretKey2026!@#$9876543210");

    bCryptPasswordEncoder = new BCryptPasswordEncoder();
    userRepo = Mockito.mock(UserRepo.class);
    userDetailsService = new CustomUserDetailsService(userRepo);
  }

  @Test
  void testJwtPasswordEncryptionAndMatching() {
    String rawPassword = "P@ssword2026!Secure";

    // Encrypt password into JWT token
    String jwtPassword = jwtPasswordEncoder.encode(rawPassword);

    // 1. Password must NOT be plaintext
    assertNotEquals(rawPassword, jwtPassword);

    // 2. Encrypted password MUST be a valid JWT token
    assertTrue(jwtPassword.startsWith("eyJ"), "Password column should start with 'eyJ' (JWT header)");
    int firstDot = jwtPassword.indexOf('.');
    int secondDot = jwtPassword.indexOf('.', firstDot + 1);
    assertTrue(firstDot > 0 && secondDot > firstDot, "Password must have standard JWT structure (header.payload.signature)");

    // 3. Password cannot be read in plaintext by base64 decoding the payload
    assertFalse(jwtPassword.contains(rawPassword), "JWT must not contain raw password in plaintext");

    // 4. Verification matches correct password
    assertTrue(jwtPasswordEncoder.matches(rawPassword, jwtPassword));

    // 5. Verification rejects incorrect password
    assertFalse(jwtPasswordEncoder.matches("WrongPassword123", jwtPassword));

    // 6. Nonce randomization: same password encoded twice produces different JWTs, but both match
    String secondJwt = jwtPasswordEncoder.encode(rawPassword);
    assertNotEquals(jwtPassword, secondJwt);
    assertTrue(jwtPasswordEncoder.matches(rawPassword, secondJwt));
  }

  @Test
  void testJwtPasswordTamperingRejected() {
    String raw = "superSecretPassword";
    String jwtPassword = jwtPasswordEncoder.encode(raw);

    // Tamper with the signature
    String tampered = jwtPassword.substring(0, jwtPassword.length() - 4) + "XXXX";
    assertFalse(jwtPasswordEncoder.matches(raw, tampered), "Tampered JWT token must be rejected");
  }

  @Test
  void testJwtPasswordFallbackAndUpgradeCheck() {
    String raw = "plainPassword123";
    String bcryptHash = bCryptPasswordEncoder.encode(raw);

    // BCrypt fallback
    assertTrue(jwtPasswordEncoder.matches(raw, bcryptHash));
    assertTrue(jwtPasswordEncoder.upgradeEncoding(bcryptHash));

    // Plaintext fallback for legacy unhashed rows
    assertTrue(jwtPasswordEncoder.matches(raw, raw));
    assertTrue(jwtPasswordEncoder.upgradeEncoding(raw));

    // Valid JWT token does not need upgrade
    String jwtToken = jwtPasswordEncoder.encode(raw);
    assertFalse(jwtPasswordEncoder.upgradeEncoding(jwtToken));
  }

  @Test
  void testAuthJwtGenerationAndValidation() {
    User user = new User("Alice", "alice@example.com", jwtPasswordEncoder.encode("secret"), Role.STUDENT);
    user.setId(10L);

    String token = jwtUtils.generateToken(user);
    assertNotNull(token);
    assertTrue(jwtUtils.validateToken(token));
    assertEquals("alice@example.com", jwtUtils.extractUsername(token));
    assertEquals("STUDENT", jwtUtils.extractRole(token));
    assertFalse(jwtUtils.isTokenExpired(token));
  }

  @Test
  void testAuthJwtWithUserDetails() {
    String jwtEncodedPassword = jwtPasswordEncoder.encode("leaderPass");
    User user = new User("Bob Leader", "bob@example.com", jwtEncodedPassword, Role.CLUB_LEADER);
    when(userRepo.findByEmail("bob@example.com")).thenReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername("bob@example.com");
    assertNotNull(userDetails);
    assertEquals("bob@example.com", userDetails.getUsername());
    assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLUB_LEADER")));

    String token = jwtUtils.generateToken(userDetails);
    assertTrue(jwtUtils.validateToken(token, userDetails));
  }

  @Test
  void testUserNotFoundThrowsException() {
    when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailsService.loadUserByUsername("unknown@example.com");
    });
  }
}
