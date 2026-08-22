package com.example.showcase.activity.util;

import com.example.showcase.activity.common.RequestContext;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Adds the fields needed to follow one Temporal activity to every log message. */
public final class RequestLogger {
  private final Logger logger;
  private final String activity;

  private RequestLogger(Class<?> source) {
    this.logger = LoggerFactory.getLogger(source);
    this.activity = source.getSimpleName().replace("Impl", "");
  }

  public static RequestLogger forClass(Class<?> source) {
    return new RequestLogger(source);
  }

  public void info(RequestContext context, String message, Object... arguments) {
    withContext(context, () -> logger.info(message, arguments));
  }

  public void warn(RequestContext context, String message, Object... arguments) {
    withContext(context, () -> logger.warn(message, arguments));
  }

  private void withContext(RequestContext context, Runnable action) {
    try (MDC.MDCCloseable requestId = MDC.putCloseable("requestId", context.requestId());
        MDC.MDCCloseable retry = MDC.putCloseable("retry", retryNumber());
        MDC.MDCCloseable activityName = MDC.putCloseable("activity", activity)) {
      action.run();
    }
  }

  private String retryNumber() {
    try {
      return Integer.toString(Activity.getExecutionContext().getInfo().getAttempt());
    } catch (IllegalStateException ignored) {
      return "n/a";
    }
  }
}
