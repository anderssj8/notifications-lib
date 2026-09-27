package com.example.notifications.api;

import java.util.Map;

public record Notification(Channel channel, String recipient, String subject, String body, String templateId,
                           Map<String, String> variables) {
    public Notification {
        variables = variables == null ? Map.of() : Map.copyOf(variables);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Channel channel;
        private String recipient;
        private String subject;
        private String body;
        private String templateId;
        private Map<String, String> variables = Map.of();

        public Builder channel(Channel v) {
            channel = v;
            return this;
        }

        public Builder recipient(String v) {
            recipient = v;
            return this;
        }

        public Builder subject(String v) {
            subject = v;
            return this;
        }

        public Builder body(String v) {
            body = v;
            return this;
        }

        public Builder template(String id, Map<String, String> vars) {
            templateId = id;
            variables = vars;
            return this;
        }

        public Notification build() {
            return new Notification(channel, recipient, subject, body, templateId, variables);
        }
    }
}
