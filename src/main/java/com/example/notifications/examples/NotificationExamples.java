package com.example.notifications.examples;

import com.example.notifications.api.*;
import com.example.notifications.config.*;
import com.example.notifications.core.NotificationLibrary;
import com.example.notifications.event.InMemoryEventPublisher;
import com.example.notifications.provider.*;
import com.example.notifications.retry.RetryPolicy;
import com.example.notifications.template.InMemoryTemplateEngine;

import java.time.Duration;
import java.util.Map;

public final class NotificationExamples {
    public static void main(String[] args) {
        var events = new InMemoryEventPublisher();
        events.subscribe(e -> System.out.printf("EVENT %-6s %s%n", e.status(), e.channel()));
        try (var client = NotificationLibrary.builder().provider(new SendGridEmailProvider(new SendGridConfig("demo-key".toCharArray(), "noreply@example.com"))).provider(new TwilioSmsProvider(new TwilioConfig("ACdemo", "demo-token".toCharArray(), "+15005550006"))).provider(new FirebasePushProvider(new FirebaseConfig("demo-project", "demo-token".toCharArray()))).templates(new InMemoryTemplateEngine(Map.of("welcome", "Hola {{name}}, bienvenida/o."))).retryPolicy(RetryPolicy.exponential(3, Duration.ofMillis(25))).eventPublisher(events).build()) {
            var email = Notification.builder().channel(Channel.EMAIL).recipient("jose@example.com").subject("Bienvenida").template("welcome", Map.of("name", "José")).build();
            System.out.println(client.sendAsync(email).join());
            var sms = Notification.builder().channel(Channel.SMS).recipient("+51999999999").body("Tu código es 1234").build();
            var push = Notification.builder().channel(Channel.PUSH).recipient("device_token_12345678901234567890").body("Tienes novedades").build();
            System.out.println(client.sendBatchAsync(java.util.List.of(sms, push)).join());
        }
    }
}
