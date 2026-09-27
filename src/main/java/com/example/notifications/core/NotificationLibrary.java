package com.example.notifications.core;

import com.example.notifications.api.*;
import com.example.notifications.event.*;
import com.example.notifications.retry.RetryPolicy;
import com.example.notifications.spi.NotificationProvider;
import com.example.notifications.template.*;
import com.example.notifications.validation.*;

import java.util.*;
import java.util.concurrent.*;

public final class NotificationLibrary {
    private NotificationLibrary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<Channel, NotificationProvider> providers = new EnumMap<>(Channel.class);
        private NotificationValidator validator = new DefaultNotificationValidator();
        private TemplateEngine templates = new InMemoryTemplateEngine(Map.of());
        private RetryPolicy retry = RetryPolicy.noRetry();
        private EventPublisher events = new InMemoryEventPublisher();
        private ExecutorService executor;

        public Builder provider(NotificationProvider p) {
            providers.put(p.channel(), p);
            return this;
        }

        public Builder validator(NotificationValidator v) {
            validator = v;
            return this;
        }

        public Builder templates(TemplateEngine t) {
            templates = t;
            return this;
        }

        public Builder retryPolicy(RetryPolicy r) {
            retry = r;
            return this;
        }

        public Builder eventPublisher(EventPublisher e) {
            events = e;
            return this;
        }

        public Builder executor(ExecutorService e) {
            executor = e;
            return this;
        }

        public NotificationClient build() {
            boolean owns = executor == null;
            if (owns) executor = Executors.newVirtualThreadPerTaskExecutor();
            return new DefaultNotificationClient(providers, validator, templates, retry, events, executor, owns);
        }
    }
}
