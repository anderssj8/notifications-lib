package com.example.notifications.provider;

import com.example.notifications.api.Channel;
import com.example.notifications.config.TwilioConfig;

import java.util.UUID;

public final class TwilioSmsProvider extends AbstractSimulatedProvider {
    private final TwilioConfig config;

    public TwilioSmsProvider(TwilioConfig c) {
        config = c;
    }

    public Channel channel() {
        return Channel.SMS;
    }

    public String name() {
        return config.providerName();
    }

    protected String externalId() {
        return "SM" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
}
