package com.github.aputintsev.architecture_cinemaabyss.events.service;

import com.github.aputintsev.architecture_cinemaabyss.events.dto.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventsConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsConsumer.class);

  @KafkaListener(
      topics = "${app.kafka.topics.payment-events:payment-events}",
      groupId = "${app.kafka.consumer.group-id:payment-events-group}"
  )
  public void consumePaymentEvent(
      @Payload PaymentEvent paymentEvent,
      @Header(KafkaHeaders.RECEIVED_KEY) String key,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp
  ) {
    LOGGER.info("Received Payment Event: key={}, partition={}, offset={}, timestamp={}", key, partition, offset, timestamp);
    LOGGER.info("Payment Event details: {}", paymentEvent);
  }
}
