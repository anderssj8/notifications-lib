package com.example.notifications.event;
public interface EventPublisher { void subscribe(DeliveryEventListener listener); void unsubscribe(DeliveryEventListener listener); void publish(DeliveryEvent event); }
