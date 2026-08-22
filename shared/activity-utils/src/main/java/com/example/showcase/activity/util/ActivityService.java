package com.example.showcase.activity.util;

import jakarta.annotation.PreDestroy;

/** Base class for a microservice that owns one Temporal activity. */
public abstract class ActivityService {
  private ActivityWorker worker;

  protected final void startWorker(
      String temporalTarget, String temporalNamespace, String taskQueue, Object activity) {
    worker = ActivityWorker.start(temporalTarget, temporalNamespace, taskQueue, activity);
  }

  @PreDestroy
  public final void stopWorker() {
    if (worker != null) {
      worker.close();
    }
  }
}
