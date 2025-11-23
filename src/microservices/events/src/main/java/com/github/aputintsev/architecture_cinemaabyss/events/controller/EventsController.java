package com.github.aputintsev.architecture_cinemaabyss.events.controller;

import static com.github.aputintsev.architecture_cinemaabyss.events.Constants.MOVIE_EVENTS_TOPIC;
import static com.github.aputintsev.architecture_cinemaabyss.events.Constants.PAYMENT_EVENTS_TOPIC;
import static com.github.aputintsev.architecture_cinemaabyss.events.Constants.USER_EVENTS_TOPIC;
import com.github.aputintsev.architecture_cinemaabyss.events.dto.Event;
import com.github.aputintsev.architecture_cinemaabyss.events.dto.EventResponse;
import com.github.aputintsev.architecture_cinemaabyss.events.dto.EventType;
import com.github.aputintsev.architecture_cinemaabyss.events.dto.MovieEvent;
import com.github.aputintsev.architecture_cinemaabyss.events.dto.PaymentEvent;
import com.github.aputintsev.architecture_cinemaabyss.events.dto.UserEvent;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventsController {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventsController.class);

  private final KafkaTemplate<String, Object>  kafkaTemplate;

  public EventsController(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @GetMapping("/health")
  public Map<String, Boolean> health() {
    return Collections.singletonMap("status", true);
  }

  @PostMapping(value = "/user")
  public ResponseEntity<EventResponse> createUserEvent(@RequestBody UserEvent userEvent) {
    try {
      String eventId = UUID.randomUUID().toString();
      SendResult<String, Object> sendResult = this.kafkaTemplate.send(USER_EVENTS_TOPIC, eventId, userEvent).get();
      return new ResponseEntity<>(new EventResponse(
          "success",
          sendResult.getRecordMetadata().partition(),
          sendResult.getRecordMetadata().offset(),
          getEvent(EventType.USER, sendResult, userEvent)
      ), HttpStatus.CREATED);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("Interrupted while waiting for user event", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Throwable e) {
      LOGGER.error("Error while creating user event", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/movie")
  public ResponseEntity<EventResponse> createMovieEvent(@RequestBody MovieEvent movieEvent) {
    try {
      String eventId = UUID.randomUUID().toString();
      SendResult<String, Object> sendResult = this.kafkaTemplate.send(MOVIE_EVENTS_TOPIC, eventId, movieEvent).get();
      return new ResponseEntity<>(new EventResponse(
          "success",
          sendResult.getRecordMetadata().partition(),
          sendResult.getRecordMetadata().offset(),
          getEvent(EventType.MOVIE, sendResult, movieEvent)
      ), HttpStatus.CREATED);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("Interrupted while waiting for movie event", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Throwable e) {
      LOGGER.error("Error while creating movie event", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/payment")
  public ResponseEntity<EventResponse> createPaymentEvent(@RequestBody PaymentEvent paymentEvent) {
    try {
      String eventId = UUID.randomUUID().toString();
      SendResult<String, Object> sendResult = this.kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, eventId, paymentEvent).get();
      return new ResponseEntity<>(new EventResponse(
          "success",
          sendResult.getRecordMetadata().partition(),
          sendResult.getRecordMetadata().offset(),
          getEvent(EventType.PAYMENT, sendResult, paymentEvent)
      ), HttpStatus.CREATED);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("Interrupted while sending event", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Throwable e) {
      LOGGER.error("Error while sending event", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private Event getEvent(EventType type, SendResult<String, Object> sendResult, Object payload) {
    Instant timestamp = Instant.ofEpochMilli(sendResult.getRecordMetadata().timestamp());
    return new Event(
        sendResult.getProducerRecord().key(),
        type,
        timestamp,
        payload
    );
  }
}
