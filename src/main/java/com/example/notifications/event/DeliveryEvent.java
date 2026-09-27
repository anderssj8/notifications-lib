package com.example.notifications.event;
import com.example.notifications.api.*; import com.example.notifications.error.ErrorCode; import java.time.Instant;
public record DeliveryEvent(String notificationId, Channel channel, SendStatus status, String provider, ErrorCode errorCode, String detail, Instant timestamp) {}
