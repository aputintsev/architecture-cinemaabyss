package com.github.aputintsev.architecture_cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserEvent(
  @JsonProperty("user_id") Integer userId,
  String username,
  String email,
  String action,
  String timestamp
) {
  @Override
  public String toString() {
    return "UserEvent{" +
        "userId=" + userId +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", action='" + action + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}
