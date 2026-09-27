package com.example.notifications.provider;
import com.example.notifications.api.Channel; import com.example.notifications.config.SendGridConfig; import java.util.UUID;
public final class SendGridEmailProvider extends AbstractSimulatedProvider { private final SendGridConfig config; public SendGridEmailProvider(SendGridConfig c){config=c;} public Channel channel(){return Channel.EMAIL;} public String name(){return config.providerName();} protected String externalId(){return "sg-"+UUID.randomUUID();} }
