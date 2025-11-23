package com.github.aputintsev.architecture_cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {
  USER("user"),
  MOVIE("movie"),
  PAYMENT("payment");

  private final String name;

  EventType(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
