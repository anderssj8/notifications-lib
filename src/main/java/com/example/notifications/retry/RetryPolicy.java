package com.example.notifications.retry;
import java.time.Duration;
public record RetryPolicy(int maxAttempts, Duration initialDelay, double multiplier) { public RetryPolicy {if(maxAttempts<1||initialDelay.isNegative()||multiplier<1)throw new IllegalArgumentException("Política de reintento inválida");} public static RetryPolicy noRetry(){return new RetryPolicy(1,Duration.ZERO,1);} public static RetryPolicy exponential(int attempts,Duration initial){return new RetryPolicy(attempts,initial,2);} }
