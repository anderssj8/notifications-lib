package com.example.notifications.config;

public sealed interface ProviderConfig permits SendGridConfig, TwilioConfig, FirebaseConfig {
    String providerName();
}
