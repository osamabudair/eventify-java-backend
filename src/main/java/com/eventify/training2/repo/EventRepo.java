package com.eventify.training2.repo;

import com.eventify.training2.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
  List<Event> findByOrganizerId (Long organizerId);

  long countByOrganizerId(Long organizerId);

  List<Event> findByLocationIgnoreCase (String location);

}
