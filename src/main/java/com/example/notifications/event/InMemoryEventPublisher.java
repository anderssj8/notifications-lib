package com.example.notifications.event;
import java.util.concurrent.CopyOnWriteArrayList;
public final class InMemoryEventPublisher implements EventPublisher { private final CopyOnWriteArrayList<DeliveryEventListener> listeners=new CopyOnWriteArrayList<>(); public void subscribe(DeliveryEventListener l){listeners.add(l);} public void unsubscribe(DeliveryEventListener l){listeners.remove(l);} public void publish(DeliveryEvent e){listeners.forEach(l->{try{l.onEvent(e);}catch(RuntimeException ignored){}});} }
