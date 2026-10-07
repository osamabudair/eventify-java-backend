package com.eventify.training2.config;

import com.eventify.training2.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

  @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}")
  private long jwtExpirationMs;

  private SecretKey getSigningKey() {
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64.decode(jwtSecret);
      if (keyBytes.length < 32) {
        keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
      }
    } catch (Exception e) {
      keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    }

    if (keyBytes.length < 32) {
      try {
        keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);
      } catch (NoSuchAlgorithmException ignored) {
      }
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    List<String> authorities = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    claims.put("roles", authorities);
    if (!authorities.isEmpty()) {
      claims.put("role", authorities.get(0).replace("ROLE_", ""));
    }
    return buildToken(claims, userDetails.getUsername());
  }

  public String generateToken(String username, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    claims.put("roles", List.of("ROLE_" + role, role));
    return buildToken(claims, username);
  }

  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", user.getId());
    claims.put("name", user.getName());
    claims.put("role", user.getRole().name());
    claims.put("roles", List.of("ROLE_" + user.getRole().name(), user.getRole().name()));
    return buildToken(claims, user.getEmail());
  }

  public String generateToken(Map<String, Object> extraClaims, String username) {
    return buildToken(extraClaims, username);
  }

  private String buildToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey())
            .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String extractRole(String token) {
    Claims claims = extractAllClaims(token);
    Object role = claims.get("role");
    return role != null ? role.toString() : null;
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public boolean isTokenExpired(String token) {
    try {
      return extractExpiration(token).before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return true;
    }
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
              .verifyWith(getSigningKey())
              .build()
              .parseSignedClaims(token);
      return !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}