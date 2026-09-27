package com.example.notifications.error;
public enum ErrorCode {
  INVALID_NOTIFICATION(false), INVALID_EMAIL(false), INVALID_PHONE(false), INVALID_DEVICE_TOKEN(false), CHANNEL_NOT_CONFIGURED(false), TEMPLATE_NOT_FOUND(false),
  SENDGRID_400(false), SENDGRID_401(false), SENDGRID_429(true), SENDGRID_500(true), TWILIO_21211(false), TWILIO_21610(false), TWILIO_20429(true), TWILIO_500(true),
  FCM_INVALID_ARGUMENT(false), FCM_UNREGISTERED(false), FCM_QUOTA_EXCEEDED(true), FCM_UNAVAILABLE(true), PROVIDER_ERROR(true);
  private final boolean retryable; ErrorCode(boolean retryable){this.retryable=retryable;} public boolean retryable(){return retryable;}
}
