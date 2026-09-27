package com.example.notifications.config;

public record SendGridConfig(char[] apiKey, String fromEmail) implements ProviderConfig {
    public SendGridConfig {
        apiKey = apiKey.clone();
        if (apiKey.length == 0 || fromEmail == null)
            throw new IllegalArgumentException("Configuración SendGrid incompleta");
    }

    public String providerName() {
        return "SendGrid";
    }

    @Override
    public char[] apiKey() {
        return apiKey.clone();
    }
}
