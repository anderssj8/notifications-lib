package com.example.notifications.config;

public record FirebaseConfig(String projectId, char[] serviceAccountToken) implements ProviderConfig {
    public FirebaseConfig {
        serviceAccountToken = serviceAccountToken.clone();
        if (projectId == null || serviceAccountToken.length == 0)
            throw new IllegalArgumentException("Configuración Firebase incompleta");
    }

    public String providerName() {
        return "Firebase Cloud Messaging";
    }

    @Override
    public char[] serviceAccountToken() {
        return serviceAccountToken.clone();
    }
}
