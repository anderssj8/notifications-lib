package com.example.notifications.core;

import com.example.notifications.api.*;
import com.example.notifications.error.*;
import com.example.notifications.event.*;
import com.example.notifications.retry.RetryPolicy;
import com.example.notifications.spi.NotificationProvider;
import com.example.notifications.template.TemplateEngine;
import com.example.notifications.validation.NotificationValidator;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public final class DefaultNotificationClient implements NotificationClient {
    private final Map<Channel, NotificationProvider> providers;
    private final NotificationValidator validator;
    private final TemplateEngine templates;
    private final RetryPolicy retry;
    private final EventPublisher events;
    private final ExecutorService executor;
    private final boolean ownsExecutor;

    public DefaultNotificationClient(Map<Channel, NotificationProvider> p, NotificationValidator v, TemplateEngine t, RetryPolicy r, EventPublisher e, ExecutorService x, boolean owns) {
        providers = Map.copyOf(p);
        validator = v;
        templates = t;
        retry = r;
        events = e;
        executor = x;
        ownsExecutor = owns;
    }

    public SendResult send(Notification original) {
        Notification n = render(original);
        validator.validate(n);
        NotificationProvider p = providers.get(n.channel());
        if (p == null)
            throw new ValidationException(ErrorCode.CHANNEL_NOT_CONFIGURED, "Canal no configurado: " + n.channel());
        String eventId = UUID.randomUUID().toString();
        events.publish(new DeliveryEvent(eventId, n.channel(), SendStatus.QUEUED, p.name(), null, "En cola", Instant.now()));
        for (int attempt = 1; attempt <= retry.maxAttempts(); attempt++) {
            try {
                SendResult raw = p.send(n);
                SendResult result = new SendResult(eventId, n.channel(), p.name(), raw.status(), raw.providerMessageId(), attempt, Instant.now());
                events.publish(new DeliveryEvent(eventId, n.channel(), result.status(), p.name(), null, "Aceptado", Instant.now()));
                return result;
            } catch (DeliveryException ex) {
                if (!ex.code().retryable() || attempt == retry.maxAttempts()) {
                    events.publish(new DeliveryEvent(eventId, n.channel(), SendStatus.FAILED, p.name(), ex.code(), ex.getMessage(), Instant.now()));
                    throw ex;
                }
                sleep(attempt);
            }
        }
        throw new DeliveryException(ErrorCode.PROVIDER_ERROR, "Fallo inesperado");
    }

    public CompletableFuture<SendResult> sendAsync(Notification n) {
        return CompletableFuture.supplyAsync(() -> send(n), executor);
    }

    public CompletableFuture<List<SendResult>> sendBatchAsync(List<Notification> ns) {
        List<CompletableFuture<SendResult>> fs = ns.stream().map(this::sendAsync).toList();
        return CompletableFuture.allOf(fs.toArray(CompletableFuture[]::new)).thenApply(v -> fs.stream().map(CompletableFuture::join).toList());
    }

    private Notification render(Notification n) {
        if (n != null && n.templateId() != null && !n.templateId().isBlank())
            return new Notification(n.channel(), n.recipient(), n.subject(), templates.render(n.templateId(), n.variables()), null, Map.of());
        return n;
    }

    private void sleep(int attempt) {
        try {
            long ms = (long) (retry.initialDelay().toMillis() * Math.pow(retry.multiplier(), attempt - 1));
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DeliveryException(ErrorCode.PROVIDER_ERROR, "Reintento interrumpido", e);
        }
    }

    public void close() {
        if (ownsExecutor) executor.shutdown();
    }
}
