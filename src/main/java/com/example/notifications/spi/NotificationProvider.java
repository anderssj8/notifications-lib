package com.example.notifications.spi;
import com.example.notifications.api.*;
public interface NotificationProvider { Channel channel(); String name(); SendResult send(Notification notification); }
