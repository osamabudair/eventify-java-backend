package com.eventify.training2.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtPasswordEncoder implements PasswordEncoder {

  @Value("${jwt.password.secret:EventifyJwtSecurePasswordSecretKey2026!@#$9876543210}")
  private String passwordSecret = "EventifyJwtSecurePasswordSecretKey2026!@#$9876543210";

  private final BCryptPasswordEncoder bCryptFallback = new BCryptPasswordEncoder();
  private final SecureRandom secureRandom = new SecureRandom();

  public JwtPasswordEncoder() {
  }

  public JwtPasswordEncoder(String passwordSecret) {
    this.passwordSecret = passwordSecret;
  }

  public void setPasswordSecret(String passwordSecret) {
    this.passwordSecret = passwordSecret;
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = passwordSecret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      try {
        keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);
      } catch (NoSuchAlgorithmException ignored) {
      }
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String hashPasswordWithSalt(String rawPassword, String salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.update(salt.getBytes(StandardCharsets.UTF_8));
      byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available", e);
    }
  }

  @Override
  public String encode(CharSequence rawPassword) {
    if (rawPassword == null) {
      throw new IllegalArgumentException("rawPassword cannot be null");
    }

    byte[] saltBytes = new byte[16];
    secureRandom.nextBytes(saltBytes);
    String salt = Base64.getEncoder().encodeToString(saltBytes);

    String passwordHash = hashPasswordWithSalt(rawPassword.toString(), salt);

    Date now = new Date();
    // 50-year expiration for stored encrypted password tokens
    Date expiry = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 365 * 50);

    return Jwts.builder()
            .id(UUID.randomUUID().toString()) // Unique Nonce
            .issuedAt(now)
            .expiration(expiry)
            .claim("salt", salt)
            .claim("pwd_hash", passwordHash)
            .claim("type", "ENCRYPTED_PASSWORD")
            .signWith(getSigningKey())
            .compact();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    if (rawPassword == null || encodedPassword == null) {
      return false;
    }

    String raw = rawPassword.toString();

    // 1. Check if encodedPassword is a JWT token (starts with eyJ and has 3 parts)
    if (encodedPassword.startsWith("eyJ") && encodedPassword.contains(".")) {
      try {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(encodedPassword)
                .getPayload();

        String salt = claims.get("salt", String.class);
        String expectedHash = claims.get("pwd_hash", String.class);

        if (salt != null && expectedHash != null) {
          String computedHash = hashPasswordWithSalt(raw, salt);
          return MessageDigest.isEqual(
                  computedHash.getBytes(StandardCharsets.UTF_8),
                  expectedHash.getBytes(StandardCharsets.UTF_8)
          );
        }
      } catch (JwtException | IllegalArgumentException e) {
        // Tampered or invalid JWT signature must be rejected immediately
        return false;
      }
    }

    // 2. BCrypt fallback for legacy hashes
    try {
      if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
        return bCryptFallback.matches(raw, encodedPassword);
      }
    } catch (Exception ignored) {
    }

    // 3. Plaintext fallback for legacy unhashed rows
    return raw.equals(encodedPassword);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    if (encodedPassword == null) {
      return true;
    }
    // If it is already a valid JWT token, no upgrade needed
    if (encodedPassword.startsWith("eyJ") && encodedPassword.contains(".")) {
      try {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(encodedPassword);
        return false;
      } catch (Exception ignored) {
        return true;
      }
    }
    return true;
  }
}

