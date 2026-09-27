package com.example.notifications.api;
import java.util.List; import java.util.concurrent.CompletableFuture;
public interface NotificationClient extends AutoCloseable {
  SendResult send(Notification notification);
  CompletableFuture<SendResult> sendAsync(Notification notification);
  CompletableFuture<List<SendResult>> sendBatchAsync(List<Notification> notifications);
  default void close() {}
}
