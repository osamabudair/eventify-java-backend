package com.eventify.training2.config;

import com.eventify.training2.model.Role;
import com.eventify.training2.model.User;
import com.eventify.training2.repo.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepo userRepo;

  public CustomUserDetailsService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepo.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

    String roleName = user.getRole() != null ? user.getRole().name() : "STUDENT";
    List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + roleName),
            new SimpleGrantedAuthority(roleName)
    );

    boolean isSuspended = (user.getRole() == Role.STUDENT && Boolean.FALSE.equals(user.getApproved()));

    return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            !isSuspended,
            true,
            true,
            !isSuspended,
            authorities
    );
  }
}