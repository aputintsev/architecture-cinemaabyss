package com.github.aputintsev.architecture_cinemaabyss.events.dto;

public record EventResponse(
  String status,
  Integer partition,
  Long offset,
  Event event
) {
}
