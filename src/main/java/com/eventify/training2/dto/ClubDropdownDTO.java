package com.eventify.training2.dto;

public class ClubDropdownDTO {
  private Long id;
  private String name;

  public ClubDropdownDTO(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
}
