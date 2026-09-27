package com.example.notifications.error;
public final class DeliveryException extends NotificationException { public DeliveryException(ErrorCode c,String m){super(c,m);} public DeliveryException(ErrorCode c,String m,Throwable t){super(c,m,t);} }
