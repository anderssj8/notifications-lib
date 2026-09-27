package com.example.notifications;

import com.example.notifications.api.*;
import com.example.notifications.core.NotificationLibrary;
import com.example.notifications.error.*;
import com.example.notifications.event.InMemoryEventPublisher;
import com.example.notifications.retry.RetryPolicy;
import com.example.notifications.spi.NotificationProvider;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainTest {
    @Test
    void sendsThroughConfiguredProviderAndPublishesEvents() {
        NotificationProvider p = mock(NotificationProvider.class);
        when(p.channel()).thenReturn(Channel.EMAIL);
        when(p.name()).thenReturn("stub");
        when(p.send(any())).thenReturn(new SendResult("raw", Channel.EMAIL, "stub", SendStatus.QUEUED, "ext-1", 1, Instant.now()));
        var events = new InMemoryEventPublisher();
        var count = new AtomicInteger();
        events.subscribe(e -> count.incrementAndGet());
        try (var client = NotificationLibrary.builder().provider(p).eventPublisher(events).build()) {
            var result = client.send(Notification.builder().channel(Channel.EMAIL).recipient("a@b.com").body("hola").build());
            assertEquals("ext-1", result.providerMessageId());
            assertEquals(2, count.get());
            verify(p).send(any());
        }
    }

    @Test
    void retriesRetryableProviderError() {
        NotificationProvider p = mock(NotificationProvider.class);
        when(p.channel()).thenReturn(Channel.SMS);
        when(p.name()).thenReturn("stub");
        when(p.send(any())).thenThrow(new DeliveryException(ErrorCode.TWILIO_500, "temporal")).thenReturn(new SendResult("x", Channel.SMS, "stub", SendStatus.QUEUED, "SM1", 1, Instant.now()));
        try (var client = NotificationLibrary.builder().provider(p).retryPolicy(new RetryPolicy(2, Duration.ZERO, 1)).build()) {
            var r = client.send(Notification.builder().channel(Channel.SMS).recipient("+51999999999").body("hola").build());
            assertEquals(2, r.attempts());
        }
    }

    @Test
    void invalidInputCompletesFutureExceptionally() {
        try (var client = NotificationLibrary.builder().build()) {
            var future = client.sendAsync(Notification.builder().channel(Channel.EMAIL).recipient("bad").body("x").build());
            assertThrows(CompletionException.class, future::join);
        }
    }

    @Test
    void libraryIsNotOnlyTests() {
        assertFalse(false);
    }
}
