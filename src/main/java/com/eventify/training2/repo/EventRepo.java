package com.eventify.training2.repo;

import com.eventify.training2.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
  List<Event> findByOrganizerId (Long organizerId);

  long countByOrganizerId(Long organizerId);

  List<Event> findByLocationIgnoreCase (String location);

  @Query("SELECT e FROM Event e WHERE " +
          "(:category IS NULL OR :category = '' OR :category = 'All' OR LOWER(e.category) = LOWER(:category)) AND " +
          "(:organizerId IS NULL OR e.organizer.id = :organizerId) AND " +
          "(:keyword IS NULL OR :keyword = '' OR " +
          "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(e.organizer.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  List<Event> exploreEvents(@Param("keyword") String keyword,
                            @Param("category") String category,
                            @Param("organizerId") Long organizerId);

}
