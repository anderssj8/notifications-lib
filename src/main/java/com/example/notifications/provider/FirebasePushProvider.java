package com.example.notifications.provider;
import com.example.notifications.api.Channel; import com.example.notifications.config.FirebaseConfig; import java.util.UUID;
public final class FirebasePushProvider extends AbstractSimulatedProvider { private final FirebaseConfig config; public FirebasePushProvider(FirebaseConfig c){config=c;} public Channel channel(){return Channel.PUSH;} public String name(){return config.providerName();} protected String externalId(){return "projects/"+config.projectId()+"/messages/"+UUID.randomUUID();} }
