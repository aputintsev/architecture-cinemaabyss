package com.github.aputintsev.architecture_cinemaabyss.events.service;

import com.github.aputintsev.architecture_cinemaabyss.events.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class UserEventsConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsConsumer.class);

  @KafkaListener(
      topics = "${app.kafka.topics.user-events:user-events}",
      groupId = "${app.kafka.consumer.group-id:user-events-group}"
  )
  public void consumeUserEvent(
      @Payload UserEvent userEvent,
      @Header(KafkaHeaders.RECEIVED_KEY) String key,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp
  ) {
    LOGGER.info("Received User Event: key={}, partition={}, offset={}, timestamp={}", key, partition, offset, timestamp);
    LOGGER.info("User Event details: {}", userEvent);
  }
}
