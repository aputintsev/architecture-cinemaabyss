package com.github.aputintsev.architecture_cinemaabyss.events.dto;

import java.time.Instant;

public record Event(
    String id,
    EventType type,
    Instant timestamp,
    Object payload
) {
}
