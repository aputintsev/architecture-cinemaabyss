package com.github.aputintsev.architecture_cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentEvent(
    @JsonProperty("payment_id") Integer paymentId,
    @JsonProperty("user_id") Integer userId,
    Float amount,
    String status,
    String timestamp,
    @JsonProperty("method_type") String methodType
) {
  @Override
  public String toString() {
    return "PaymentEvent{" +
        "paymentId=" + paymentId +
        ", userId=" + userId +
        ", amount=" + amount +
        ", status='" + status + '\'' +
        ", timestamp='" + timestamp + '\'' +
        ", methodType='" + methodType + '\'' +
        '}';
  }
}
