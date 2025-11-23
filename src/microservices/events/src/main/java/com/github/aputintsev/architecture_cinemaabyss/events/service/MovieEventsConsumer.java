package com.github.aputintsev.architecture_cinemaabyss.events.service;

import com.github.aputintsev.architecture_cinemaabyss.events.dto.MovieEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MovieEventsConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(MovieEventsConsumer.class);

  @KafkaListener(
      topics = "${app.kafka.topics.movie-events:movie-events}",
      groupId = "${app.kafka.consumer.group-id:movie-events-group}"
  )
  public void consumeMovieEvent(
      @Payload MovieEvent movieEvent,
      @Header(KafkaHeaders.RECEIVED_KEY) String key,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp
  ) {
    LOGGER.info("Received Movie Event: key={}, partition={}, offset={}, timestamp={}", key, partition, offset, timestamp);
    LOGGER.info("Movie Event details: {}", movieEvent);
  }
}
