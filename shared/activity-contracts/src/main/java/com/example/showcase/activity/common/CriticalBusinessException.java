package com.example.showcase.activity.common;

/** A business failure that must never be retried. */
public class CriticalBusinessException extends RuntimeException {
  public CriticalBusinessException(String message) {
    super(message);
  }
}
