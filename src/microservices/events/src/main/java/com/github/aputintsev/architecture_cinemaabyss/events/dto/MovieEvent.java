package com.github.aputintsev.architecture_cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public record MovieEvent(
    @JsonProperty("movie_id") Integer movieId,
    String title,
    String action,
    @JsonProperty("user_id") Integer userId,
    Float rating,
    String[] genres,
    String description
    ) {
  @Override
  public String toString() {
    return "MovieEvent{" +
        "movieId=" + movieId +
        ", title='" + title + '\'' +
        ", action='" + action + '\'' +
        ", userId=" + userId +
        ", rating=" + rating +
        ", genres=" + Arrays.toString(genres) +
        ", description='" + description + '\'' +
        '}';
  }
}
