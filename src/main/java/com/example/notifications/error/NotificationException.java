package com.example.notifications.error;
public abstract class NotificationException extends RuntimeException {
  private final ErrorCode code; protected NotificationException(ErrorCode c,String m){super(m);code=c;} protected NotificationException(ErrorCode c,String m,Throwable t){super(m,t);code=c;}
  public ErrorCode code(){return code;}
}
