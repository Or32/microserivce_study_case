package com.example.showcase.activity.validation;

import com.example.showcase.activity.common.CriticalBusinessException;
import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.util.RequestLogger;
import io.temporal.failure.ApplicationFailure;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class ValidationActivityImpl implements ValidationActivity {
  private static final RequestLogger LOG = RequestLogger.forClass(ValidationActivityImpl.class);
  private final ConcurrentHashMap<String, AtomicInteger> attempts = new ConcurrentHashMap<>();

  @Override
  public void validate(RequestContext context) {
    String requestId = context.requestId();
    if (requestId.startsWith("critical-")) {
      throw ApplicationFailure.newNonRetryableFailure(
          "Request violates a critical business rule", CriticalBusinessException.class.getName());
    }

    int attempt =
        attempts.computeIfAbsent(requestId, ignored -> new AtomicInteger()).incrementAndGet();
    if (requestId.startsWith("retry-") && attempt == 1) {
      LOG.warn(context, "Simulating a transient validation failure; Temporal will retry it");
      throw new IllegalStateException("Temporary downstream outage");
    }

    LOG.info(context, "Shared validation completed");
  }
}
