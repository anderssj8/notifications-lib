package com.example.notifications.api;

import java.time.Instant;

public record SendResult(String notificationId, Channel channel, String provider, SendStatus status,
                         String providerMessageId, int attempts, Instant timestamp) {
}
